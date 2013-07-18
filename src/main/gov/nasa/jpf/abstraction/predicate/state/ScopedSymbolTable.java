package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.concrete.ConcretePath;
import gov.nasa.jpf.vm.MethodInfo;

import java.util.Set;
import java.util.Stack;

public class ScopedSymbolTable implements SymbolTable, Scoped {
	private Stack<FlatSymbolTable> scopes = new Stack<FlatSymbolTable>();
	
	@Override
	public FlatSymbolTable createDefaultScope(MethodInfo method) {
		return new FlatSymbolTable();
	}

	@Override
	public Set<AccessPath> lookupAccessPaths(AccessPath prefix) {
		return scopes.lastElement().lookupAccessPaths(prefix);
	}

	@Override
	public Set<AccessPath> lookupEquivalentAccessPaths(CompleteVariableID number) {
		return scopes.lastElement().lookupEquivalentAccessPaths(number);
	}
	
	@Override
	public Set<AccessPath> lookupEquivalentAccessPaths(AccessPath path) {
		return scopes.lastElement().lookupEquivalentAccessPaths(path);
	}

	@Override
	public CompleteVariableID resolvePath(AccessPath path) {
		return scopes.lastElement().resolvePath(path);
	}
	
	@Override
	public void processLoad(ConcretePath from) {
		scopes.lastElement().processLoad(from);
	}
	
	@Override
	public Set<AccessPath> processStore(ConcretePath from, ConcretePath to) {
		return scopes.lastElement().processStore(from, to);
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
	public void restore(Scope scope) {
		if (scope instanceof FlatSymbolTable) {
			scopes.pop();
			scopes.push((FlatSymbolTable) scope.clone());
		} else {
			throw new RuntimeException("Invalid scope type being pushed!");
		}
	}
	
	@Override
	public FlatSymbolTable memorize() {
		return scopes.lastElement().clone();
	}
	
	@Override
	public String toString() {
		return scopes.lastElement().toString();
	}

	@Override
	public int count() {
		return scopes.size();
	}
	
}
