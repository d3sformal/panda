package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.concrete.VariableID;
import gov.nasa.jpf.vm.MethodInfo;

import java.util.Set;

public class ScopedSymbolTable implements SymbolTable, Scoped {
	private SymbolTableStack scopes = new SymbolTableStack();
	
	@Override
	public FlatSymbolTable createDefaultScope(MethodInfo method) {
		return new FlatSymbolTable();
	}

	@Override
	public Set<AccessPath> lookupAccessPaths(AccessPath prefix) {
		return scopes.top().lookupAccessPaths(prefix);
	}

	@Override
	public Set<AccessPath> lookupEquivalentAccessPaths(VariableID var) {
		return scopes.top().lookupEquivalentAccessPaths(var);
	}
	
	@Override
	public Set<AccessPath> lookupEquivalentAccessPaths(AccessPath path) {
		return scopes.top().lookupEquivalentAccessPaths(path);
	}
	
	@Override
	public void processLoad(ConcretePath from) {
		scopes.top().processLoad(from);
	}
	
	@Override
	public Set<AccessPath> processPrimitiveStore(ConcretePath to) {
		return scopes.top().processPrimitiveStore(to);
	}
	
	@Override
	public Set<AccessPath> processObjectStore(Expression from, ConcretePath to) {
		return scopes.top().processObjectStore(from, to);
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
	public boolean isArray(AccessPath path) {
		return scopes.top().isArray(path);
	}
	
}
