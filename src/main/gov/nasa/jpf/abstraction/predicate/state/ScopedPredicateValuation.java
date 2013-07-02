package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.predicate.grammar.Predicate;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Stack;

public class ScopedPredicateValuation implements PredicateValuation {
	private static ScopedPredicateValuation instance;
	
	private Stack<FlatPredicateValuation> scopes = new Stack<FlatPredicateValuation>();

	private ScopedPredicateValuation() {
		scopes.push(Trace.getInstance().top().predicateValuation.clone()); //initial
	}
	
	public static ScopedPredicateValuation getInstance() {
		if (instance == null) {
			instance = new ScopedPredicateValuation();
		}
		
		return instance;
	}

	@Override
	public void put(Predicate predicate, TruthValue value) {
		scopes.lastElement().put(predicate, value);
	}

	@Override
	public TruthValue get(Predicate predicate) {
		return scopes.lastElement().get(predicate);
	}

	@Override
	public Iterator<Entry<Predicate, TruthValue>> iterator() {
		return scopes.lastElement().iterator();
	}
	
	public void restore(FlatPredicateValuation scope) {
		scopes.pop();
		scopes.push(scope.clone());
	}
	
	public FlatPredicateValuation memorize() {
		return scopes.lastElement().clone();
	}

}
