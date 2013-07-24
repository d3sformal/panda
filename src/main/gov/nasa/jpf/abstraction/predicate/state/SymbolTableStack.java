package gov.nasa.jpf.abstraction.predicate.state;

import java.util.ArrayList;
import java.util.List;

public class SymbolTableStack implements Scopes {
	
	private List<FlatSymbolTable> scopes = new ArrayList<FlatSymbolTable>();

	@Override
	public FlatSymbolTable top() {
		return scopes.get(scopes.size() - 1);
	}

	@Override
	public void pop() {
		scopes.remove(scopes.size() - 1);
	}

	@Override
	public void push(Scope scope) {
		if (scope instanceof FlatSymbolTable) {
			scopes.add((FlatSymbolTable) scope);
		} else {
			throw new RuntimeException("Invalid scope type being pushed!");
		}
	}
	
	@Override
	public int count() {
		return scopes.size();
	}
		
	@Override
	public SymbolTableStack clone() {
		SymbolTableStack clone = new SymbolTableStack();
		
		for (FlatSymbolTable scope : scopes) {
			clone.push(scope.clone());
		}
		
		return clone;
	}

}
