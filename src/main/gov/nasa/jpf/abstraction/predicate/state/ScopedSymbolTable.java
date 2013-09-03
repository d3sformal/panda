package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import java.util.Set;

public class ScopedSymbolTable implements SymbolTable, Scoped {
	private SymbolTableStack scopes = new SymbolTableStack();
	private PredicateAbstraction abstraction;
	
	public ScopedSymbolTable(PredicateAbstraction abstraction) {
		this.abstraction = abstraction;
		
		// Scope for passing what has been statically initialised
		// without this all static initialisations return and remove their scope without writing it anywhere else
		scopes.push(new FlatSymbolTable(abstraction));
	}

	@Override
	public FlatSymbolTable createDefaultScope(ThreadInfo threadInfo, MethodInfo method) {
		FlatSymbolTable ret = new FlatSymbolTable(abstraction);
		
		LocalVarInfo[] locals = method.getLocalVars();
		
		if (locals != null) {
			for (LocalVarInfo local : locals) {
				if (local.isNumeric()) {
					ret.addPrimitiveLocal(local.getName());
				} else {
					ret.addHeapValueLocal(local.getName());
				}
			}
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
	
	@Override
	public AffectedAccessExpressions processMethodCall(ThreadInfo threadInfo, StackFrame before, StackFrame after, SideEffect sideEffect) {
		MethodInfo method = after.getMethodInfo();
		
		FlatSymbolTable originalScope = scopes.top();
		FlatSymbolTable transitionScope = createDefaultScope(threadInfo, method);
		
		transitionScope.updateUniverse(scopes.top());
		
		scopes.push(transitionScope);
		
		transitionScope.addClass(method.getClassName(), threadInfo, method.getClassInfo().getStaticElementInfo());
		
		StackFrame sf = threadInfo.getTopFrame();
		Object attrs[] = sf.getArgumentAttrs(method);
		LocalVarInfo args[] = method.getArgumentLocalVars();

		if (args != null && attrs != null) {
			for (int i = 0; i < args.length; ++i) {
				Attribute attr = (Attribute) attrs[i];
				
				if (attr == null) attr = new EmptyAttribute();
				
				if (args[i] != null) {					
					if (args[i].isNumeric()) {
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
		return processVoidMethodReturn(threadInfo, before, after, sideEffect);
	}
	
	@Override
	public AffectedAccessExpressions processVoidMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after, SideEffect sideEffect) {
		AffectedAccessExpressions ret = new AffectedAccessExpressions();
		MethodInfo method = after.getMethodInfo();
		
		FlatSymbolTable transitionScope = scopes.top(1);
		
		if (RunDetector.isRunning()) {			
			Set<AccessExpression> modifications = transitionScope.getModifiedObjectAccessExpressions(scopes.top());
			
			ret.addAll(modifications);
			
			System.out.println("Objects modified in child scope after return from " + before.getClassName() + "." + before.getMethodName() + ": " + modifications);
			System.out.println(transitionScope);
			System.out.println("================================================================");
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
	
}
