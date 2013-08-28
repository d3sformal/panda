package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import java.util.Set;

public class ScopedSymbolTable implements SymbolTable, Scoped {
	private SymbolTableStack scopes = new SymbolTableStack();

	@Override
	public FlatSymbolTable createDefaultScope(MethodInfo method) {
		FlatSymbolTable ret = new FlatSymbolTable();
		
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
	public Set<AccessExpression> processPrimitiveStore(AccessExpression to) {
		return scopes.top().processPrimitiveStore(to);
	}
	
	@Override
	public Set<AccessExpression> processObjectStore(Expression from, AccessExpression to) {
		return scopes.top().processObjectStore(from, to);
	}
	
	@Override
	public void processMethodCall(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
		MethodInfo method = after.getMethodInfo();
		
		FlatSymbolTable transitionScope;
		
		if (scopes.count() == 0) {
			transitionScope = createDefaultScope(method);
		} else {
			transitionScope = scopes.top().clone();
		}
		
		scopes.push(transitionScope);
		
		//transitionScope.removeLocals();
		
		StackFrame sf = threadInfo.getTopFrame();
		Object attrs[] = sf.getArgumentAttrs(method);
		LocalVarInfo args[] = method.getArgumentLocalVars();

		if (args != null && attrs != null) {
			// Skip "this"
			for (int i = 1; i < args.length; ++i) {
				Attribute attr = (Attribute) attrs[i];
				
				if (attr == null) attr = new EmptyAttribute();
				
				if (args[i] != null) {
					if (args[i].isNumeric()) {
						// Assign to numeric (primitive) arg
						processPrimitiveStore(DefaultRoot.create(args[i].getName()));
					} else {
						ElementInfo ei = threadInfo.getElementInfo(sf.peek(args.length - i));
						
						// Assign to object arg
						processObjectStore(null, DefaultRoot.create(args[i].getName()));
					}
				}
			}
		}
	}
	
	@Override
	public void processMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
		processVoidMethodReturn(threadInfo, before, after);
	}
	
	@Override
	public void processVoidMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
		MethodInfo method = after.getMethodInfo();
		
		if (RunDetector.isRunning()) {
			FlatSymbolTable transitionScope;
			
			if (scopes.count() == 1) {
				transitionScope = createDefaultScope(method);
			} else {
				transitionScope = scopes.top(1);
			}
			
			//transitionScope.setStatics(scopes.top().getStatics());
			//transitionScope.update();
		}
		
		scopes.pop();
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
