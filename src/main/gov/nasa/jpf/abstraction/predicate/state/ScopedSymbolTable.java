package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ReturnValue;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultReturnValue;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.concrete.Reference;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Universe;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import java.util.Set;

/**
 * Symbol table aware of method call scope changes
 */
public class ScopedSymbolTable implements SymbolTable, Scoped {
	/**
	 * Stack of scopes (pushed by invoke, poped by return)
	 */
	private SymbolTableStack scopes = new SymbolTableStack();
	private PredicateAbstraction abstraction;
	
	public ScopedSymbolTable(PredicateAbstraction abstraction) {
		this.abstraction = abstraction;
		
		// Scope for passing what has been statically initialised
		// without this all static initialisations return and remove their scope without writing it anywhere else
		scopes.push(new FlatSymbolTable(abstraction));
	}

	/**
	 * Create a scope for a given method
	 */
	@Override
	public FlatSymbolTable createDefaultScope(ThreadInfo threadInfo, MethodInfo method) {
		FlatSymbolTable ret = new FlatSymbolTable(abstraction);
		
		LocalVarInfo[] locals = method.getLocalVars() == null ? new LocalVarInfo[0] : method.getLocalVars();
		
		/**
		 * Register new local variables
		 */
		for (LocalVarInfo local : locals) {
			if (local.isNumeric() || local.isBoolean()) {
				ret.addPrimitiveLocal(local.getName());
			} else {
				ret.addHeapValueLocal(local.getName());
			}
		}
		
		/**
		 * Handle main(String[] args)
		 */
		VM vm = threadInfo.getVM();
		String target = vm.getConfig().getTarget();
		
		if (method.getFullName().equals(target + ".main([Ljava/lang/String;)V")) {
			StackFrame sf = threadInfo.getTopFrame();
			
			ElementInfo ei = threadInfo.getElementInfo(sf.getLocalVariable(0));
			int length = ei.arrayLength();
			
			LocalVarInfo args = method.getArgumentLocalVars()[0];

			ret.processObjectStore(AnonymousArray.create(new Reference(threadInfo, ei), Constant.create(length)), DefaultRoot.create(args.getName()));
		}
		
		return ret;
	}
	
	@Override
	public Set<AccessExpression> processPrimitiveStore(Expression from, AccessExpression to) {
		return scopes.top().processPrimitiveStore(from, to);
	}
	
	@Override
	public Set<AccessExpression> processObjectStore(Expression from, AccessExpression to) {
		return scopes.top().processObjectStore(from, to);
	}
	
	/**
	 * At a method call it is necessary to change the scope correspondingly
	 */
	@Override
	public AffectedAccessExpressions processMethodCall(ThreadInfo threadInfo, StackFrame before, StackFrame after, SideEffect sideEffect) {
		MethodInfo method = after.getMethodInfo();
		
		FlatSymbolTable originalScope = scopes.top();
		FlatSymbolTable transitionScope = createDefaultScope(threadInfo, method);
		
		transitionScope.updateUniverse(scopes.top());
		
		scopes.push(transitionScope);

		/**
		 * Ensure that all statics are present for the current class (class in which the method is defined)
		 * 
		 * This will not take long if it already exists
		 */
		transitionScope.addClass(method.getClassName(), threadInfo, method.getClassInfo().getStaticElementInfo());
		
		Object attrs[] = before.getArgumentAttrs(method);
		LocalVarInfo args[] = method.getArgumentLocalVars();

		/**
		 * Assign values to the formal parameters according to the actual parameters
		 */
		if (args != null && attrs != null) {
			for (int i = 0; i < args.length; ++i) {
				Attribute attr = (Attribute) attrs[i];
				
				attr = Attribute.ensureNotNull(attr);
				
				if (args[i] != null) {					
					if (args[i].isNumeric() || args[i].isBoolean()) {
						// Assign to numeric (primitive) arg
						transitionScope.processPrimitiveStore(attr.getExpression(), originalScope, DefaultRoot.create(args[i].getName()));
					} else {						
						// Assign to object arg
						transitionScope.processObjectStore(attr.getExpression(), originalScope, DefaultRoot.create(args[i].getName()));
					}
				}
			}
		}
		
		return null;
	}
	
	@Override
	public AffectedAccessExpressions processMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after, SideEffect sideEffect) {
		ReturnValue calleeReturnValue = DefaultReturnValue.create();
		ReturnValue callerReturnValue = DefaultReturnValue.create(threadInfo.getPC(), before.getMethodInfo().isReferenceReturnType());

		Attribute attr = Attribute.ensureNotNull((Attribute) after.getOperandAttr());
		Expression returnExpression = attr.getExpression();
		//after.setOperandAttr(new NonEmptyAttribute(null, callerReturnValue)); // This is performed by the predicate valuation after it uses the original expression

		/**
		 * Register the return value
		 */
		if (before.getMethodInfo().isReferenceReturnType()) {
			scopes.top().addHeapValueReturn(calleeReturnValue);
			scopes.top().processObjectStore(returnExpression, calleeReturnValue);
			scopes.top(1).addHeapValueReturn(callerReturnValue);
			scopes.top(1).processObjectStore(calleeReturnValue, scopes.top(), callerReturnValue);
		} else {
			scopes.top().addPrimitiveReturn(calleeReturnValue);
			scopes.top().processPrimitiveStore(returnExpression, calleeReturnValue);
			scopes.top(1).addPrimitiveReturn(callerReturnValue);
			scopes.top(1).processPrimitiveStore(calleeReturnValue, scopes.top(), callerReturnValue);
		}

		return processVoidMethodReturn(threadInfo, before, after, sideEffect);
	}
	
	@Override
	public AffectedAccessExpressions processVoidMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after, SideEffect sideEffect) {
		AffectedAccessExpressions ret = new AffectedAccessExpressions();
		
		FlatSymbolTable transitionScope = scopes.top(1);
		
		RunDetector.detectRunning(VM.getVM(), after.getPC(), before.getPC());

		/**
		 * Detect what objects accessible in the caller scope were modified in the callee
		 */
		if (RunDetector.isRunning()) {			
			Set<AccessExpression> modifications = transitionScope.getModifiedObjectAccessExpressions(scopes.top());
			
			ret.addAll(modifications);
		}
		
		transitionScope.updateUniverse(scopes.top());
		
		scopes.pop();
		
		return ret;
	}
	
	@Override
	public void store(Scope scope) {
		if (scope instanceof FlatSymbolTable) {
			scopes.push((FlatSymbolTable) scope.clone());
		} else {
			throw new RuntimeException("Invalid scope type being pushed!");
		}
	}
	
	@Override
	public void restore(Scopes scopes) {
		if (scopes instanceof SymbolTableStack) {
			this.scopes = (SymbolTableStack) scopes.clone();
		} else {
			throw new RuntimeException("Invalid scopes type being restored!");
		}
	}
	
	@Override
	public SymbolTableStack memorize() {
		return scopes.clone();
	}
	
	@Override
	public String toString() {
		return scopes.count() > 0 ? scopes.top().toString() : "";
	}

	@Override
	public int count() {
		return scopes.count() > 0 ? scopes.top().count() : 0;
	}

	@Override
	public boolean isArray(AccessExpression path) {
		return scopes.top().isArray(path);
	}

	@Override
	public boolean isObject(AccessExpression path) {
		return scopes.top().isObject(path);
	}

	@Override
	public boolean isPrimitive(AccessExpression path) {
		return scopes.top().isPrimitive(path);
	}

	@Override
	public Universe getUniverse() {
		return scopes.top().getUniverse();
	}

    @Override
    public FlatSymbolTable get(int depth) {
        return scopes.top(depth);
    }
	
}
