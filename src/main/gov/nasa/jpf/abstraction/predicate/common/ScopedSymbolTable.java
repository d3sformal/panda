package gov.nasa.jpf.abstraction.predicate.common;

import java.util.Set;
import java.util.Stack;

public class ScopedSymbolTable implements SymbolTable {
	
	private static ScopedSymbolTable instance;
	
	private Stack<FlatSymbolTable> scopes = new Stack<FlatSymbolTable>();

	private ScopedSymbolTable() {
		scopes.push(Trace.getInstance().top().symbolTable.clone()); //initial
	}
	
	public static ScopedSymbolTable getInstance() {
		if (instance == null) {
			instance = new ScopedSymbolTable();
		}
		
		return instance;
	}
	

	@Override
	public Set<AccessPath> lookupAccessPaths(AccessPath prefix) {
		return scopes.lastElement().lookupAccessPaths(prefix);
	}

	@Override
	public Set<AccessPath> lookupEquivalentAccessPaths(VariableID number) {
		return scopes.lastElement().lookupEquivalentAccessPaths(number);
	}

	@Override
	public VariableID resolvePath(AccessPath path) {
		return scopes.lastElement().resolvePath(path);
	}
	
	@Override
	public void register(AccessPath path, VariableID number) {
		scopes.lastElement().register(path, number);
	}
	
	public void methodCall() {
		scopes.push(new FlatSymbolTable());
	}
	
	public void methodReturn() {
		if (scopes.size() > 1) {
			scopes.pop();
		}
	}
	
	public void recover(FlatSymbolTable scope) {
		scopes.pop();
		scopes.push(scope.clone());
	}
	
	public FlatSymbolTable memorise() {
		return scopes.lastElement().clone();
	}
	
}
