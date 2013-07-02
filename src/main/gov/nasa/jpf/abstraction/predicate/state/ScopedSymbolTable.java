package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.predicate.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.predicate.grammar.AccessPath;

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
	public Set<AccessPath> lookupEquivalentAccessPaths(CompleteVariableID number) {
		return scopes.lastElement().lookupEquivalentAccessPaths(number);
	}

	@Override
	public CompleteVariableID resolvePath(AccessPath path) {
		return scopes.lastElement().resolvePath(path);
	}
	
	@Override
	public void registerPathToVariable(AccessPath path, CompleteVariableID number) {
		scopes.lastElement().registerPathToVariable(path, number);
	}
	
	public void processMethodCall() {
		scopes.push(new FlatSymbolTable());
	}
	
	public void processMethodReturn() {
		if (scopes.size() > 1) {
			scopes.pop();
		}
	}
	
	public void restore(FlatSymbolTable scope) {
		scopes.pop();
		scopes.push(scope.clone());
	}
	
	public FlatSymbolTable memorize() {
		return scopes.lastElement().clone();
	}
	
}
