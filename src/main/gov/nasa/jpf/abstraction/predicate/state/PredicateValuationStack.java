package gov.nasa.jpf.abstraction.predicate.state;

import java.util.ArrayList;
import java.util.List;

public class PredicateValuationStack implements Scopes {
	
	private List<FlatPredicateValuation> scopes = new ArrayList<FlatPredicateValuation>();

	@Override
	public FlatPredicateValuation top() {
		return scopes.get(scopes.size() - 1);
	}

	@Override
	public void pop() {
		scopes.remove(scopes.size() - 1);
	}

	@Override
	public void push(Scope scope) {
		if (scope instanceof FlatPredicateValuation) {
			scopes.add((FlatPredicateValuation) scope);
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
		
		for (FlatPredicateValuation scope : scopes) {
			clone.push(scope.clone());
		}
		
		return clone;
	}

}