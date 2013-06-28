package gov.nasa.jpf.abstraction.predicate.common;

import java.util.List;
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

	// TODO: Number should wrap stack / heap / static data
	public List<AccessPath> lookupEquivalentAccessPaths(VariableID number) {
		return scopes.lastElement().lookupEquivalentAccessPaths(number);
	}

	public VariableID resolvePath(AccessPath path) {
		return scopes.lastElement().resolvePath(path);
	}
	
	public void methodCall() {
		scopes.push(new FlatSymbolTable());

		//TODO:
		//method call removes local variables (all paths rooted in local var)
		//and all reverse mappings whose root was local var
		//
		// static int A a;
		//
		// Some scope:
		// {
		//		... // access path `a' refers static field
		//		m(int a):
		//			... // access path `a' needs to be rewritten
		//		...
		// }
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

	@Override
	public void register(AccessPath path, VariableID number) {
		scopes.lastElement().register(path, number);
	}
	
}
