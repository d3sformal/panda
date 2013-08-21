package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultAccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteAccessExpression;
import gov.nasa.jpf.abstraction.concrete.access.impl.LocalVar;
import gov.nasa.jpf.abstraction.concrete.access.impl.LocalVarRootedHeapObject;
import gov.nasa.jpf.abstraction.concrete.VariableID;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ScopedSymbolTable implements SymbolTable, Scoped {
	private SymbolTableStack scopes = new SymbolTableStack();
	
	@Override
	public FlatSymbolTable createDefaultScope(MethodInfo method) {
		return new FlatSymbolTable();
	}

	@Override
	public Set<AccessExpression> lookupAccessPaths(AccessExpression prefix) {
		return scopes.top().lookupAccessPaths(prefix);
	}

	@Override
	public Set<AccessExpression> lookupEquivalentAccessPaths(VariableID var) {
		return scopes.top().lookupEquivalentAccessPaths(var);
	}
	
	@Override
	public Set<AccessExpression> lookupEquivalentAccessPaths(AccessExpression path) {
		return scopes.top().lookupEquivalentAccessPaths(path);
	}
	
	@Override
	public void processLoad(ConcreteAccessExpression from) {
		scopes.top().processLoad(from);
	}
	
	@Override
	public Set<AccessExpression> processPrimitiveStore(ConcreteAccessExpression to) {
		return scopes.top().processPrimitiveStore(to);
	}
	
	@Override
	public Set<AccessExpression> processObjectStore(Expression from, ConcreteAccessExpression to) {
		return scopes.top().processObjectStore(from, to);
	}
	
	@Override
	public void processMethodCall(ThreadInfo threadInfo, MethodInfo method) {
		FlatSymbolTable transitionScope;
		FlatSymbolTable finalScope = createDefaultScope(method);
		
		if (scopes.count() == 0) {
			transitionScope = createDefaultScope(method);
		} else {
			transitionScope = scopes.top().clone();
		}
		
		scopes.push(transitionScope);
		
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
						processPrimitiveStore(LocalVar.create(args[i].getName(), threadInfo, args[i]));
					} else {
						ElementInfo ei = threadInfo.getElementInfo(sf.peek(args.length - i));
						
						// Assign to object arg
						processObjectStore(attr.getExpression(), LocalVarRootedHeapObject.create(args[i].getName(), threadInfo, ei, args[i]));
					}
				}
			}
		
			// Transfer only the relevant symbols
			for (Map.Entry<AccessExpression, Set<VariableID>> entry : scopes.top()) {
				for (int i = 0; i < args.length; ++i) {
					if (DefaultAccessExpression.createFromString(args[i].getName()).isPrefixOf(entry.getKey())) {
						finalScope.setPathToVars(entry.getKey(), entry.getValue());
					}
				}
			}
		}
		
		scopes.pop();
		scopes.push(finalScope);
	}
	
	@Override
	public void processMethodReturn() {
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
	public boolean isObject(AccessExpression path) {
		return scopes.top().isObject(path);
	}

	@Override
	public boolean isArray(AccessExpression path) {
		return scopes.top().isArray(path);
	}

	@Override
	public Iterator<Entry<AccessExpression, Set<VariableID>>> iterator() {
		return scopes.top().iterator();
	}

	@Override
	public void setPathToVars(AccessExpression path, Set<VariableID> vars) {
		scopes.top().setPathToVars(path, vars);
	}
	
}
