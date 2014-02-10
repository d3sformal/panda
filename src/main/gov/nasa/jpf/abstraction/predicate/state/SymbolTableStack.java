package gov.nasa.jpf.abstraction.predicate.state;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.jpf.abstraction.util.Pair;

/**
 * Stack keeping Symbol Table scopes
 * 
 * method call = push
 * method return = pop
 */
public class SymbolTableStack implements Scopes {
	
	private List<Pair<String, FlatSymbolTable>> scopes = new ArrayList<Pair<String, FlatSymbolTable>>();

	@Override
	public FlatSymbolTable top() {
		return top(0);
	}

	@Override
	public void pop() {
		scopes.remove(scopes.size() - 1);
	}

	@Override
	public void push(String name, Scope scope) {
		if (scope instanceof FlatSymbolTable) {
			scopes.add(new Pair<String, FlatSymbolTable>(name, (FlatSymbolTable) scope));
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
		
		for (Pair<String, FlatSymbolTable> scope : scopes) {
			clone.push(scope.getFirst(), scope.getSecond().clone());
		}
		
		return clone;
	}

	@Override
	public FlatSymbolTable top(int i) {
		return scopes.get(scopes.size() - i - 1).getSecond();
	}

    @Override
    public void print() {
        for (Pair<String, FlatSymbolTable> scope : scopes) {
            System.out.println(scope.getFirst());
        }
    }

}
