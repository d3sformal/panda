package gov.nasa.jpf.abstraction.predicate.state;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.jpf.abstraction.util.Pair;

/**
 * Stack keeping Predicate Valuation scopes
 * 
 * method call = push
 * method return = pop
 */
public class PredicateValuationStack implements Scopes {
	
	private List<Pair<String, FlatPredicateValuation>> scopes = new ArrayList<Pair<String, FlatPredicateValuation>>();

	@Override
	public FlatPredicateValuation top() {
		return top(0);
	}

	@Override
	public void pop() {
		scopes.remove(scopes.size() - 1);
	}

	@Override
	public void push(String name, Scope scope) {
		if (scope instanceof FlatPredicateValuation) {
			scopes.add(new Pair<String, FlatPredicateValuation>(name, (FlatPredicateValuation) scope));
		} else {
			throw new RuntimeException("Invalid scope type being pushed!");
		}
	}
	
	@Override
	public int count() {
		return scopes.size();
	}
		
	@Override
	public PredicateValuationStack clone() {
		PredicateValuationStack clone = new PredicateValuationStack();
		
		for (Pair<String, FlatPredicateValuation> scope : scopes) {
			clone.push(scope.getFirst(), scope.getSecond().clone());
		}
		
		return clone;
	}

	@Override
	public FlatPredicateValuation top(int i) {
		return scopes.get(scopes.size() - i - 1).getSecond();
	}

    @Override
    public void print() {
        for (Pair<String, FlatPredicateValuation> scope : scopes) {
            System.out.println(scope.getFirst());
        }
    }

}
