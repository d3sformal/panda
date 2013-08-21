package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultAccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteAccessExpression;
import gov.nasa.jpf.abstraction.concrete.VariableID;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.MethodInfo;

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
	public void prepareMethodParamAssignment(MethodInfo method) {
		scopes.push(scopes.top().clone());
	}
	
	@Override
	public void processMethodParamAssignment(MethodInfo method) {
		FlatSymbolTable scope = scopes.top().clone();
		LocalVarInfo args[] = method.getArgumentLocalVars();
		
		scopes.push(createDefaultScope(method));
		
		for (Map.Entry<AccessExpression, Set<VariableID>> entry : scope) {
			for (LocalVarInfo arg : args) {
				if (DefaultAccessExpression.createFromString(arg.getName()).isPrefixOf(entry.getKey())) {
					scopes.top().setPathToVars(entry.getKey(), entry.getValue());
				}
			}
		}
	}
	
	@Override
	public void processMethodCall(MethodInfo method) {
		scopes.push(createDefaultScope(method));
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
