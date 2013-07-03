package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.predicate.grammar.Context;
import gov.nasa.jpf.abstraction.predicate.grammar.Predicate;
import gov.nasa.jpf.abstraction.predicate.grammar.Predicates;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Stack;

public class ScopedPredicateValuation implements PredicateValuation, Scoped {
	private static ScopedPredicateValuation instance;
	
	private Stack<FlatPredicateValuation> scopes = new Stack<FlatPredicateValuation>();
	private Predicates predicateSet = null;

	private ScopedPredicateValuation() {
	}
	
	public static ScopedPredicateValuation getInstance() {
		if (instance == null) {
			instance = new ScopedPredicateValuation();
		}
		
		return instance;
	}
	
	public FlatPredicateValuation createDefaultPredicateValuation() {
		FlatPredicateValuation valuation = new FlatPredicateValuation();
		
		for (Context context : predicateSet.contexts) {
			for (Predicate predicate : context.predicates) {
				valuation.put(predicate, TruthValue.UNDEFINED);
			}
		}
		
		return valuation;
	}
	
	public void setPredicateSet(Predicates predicateSet) {
		this.predicateSet = predicateSet;
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
	
	@Override
	public void processMethodCall() {
		scopes.push(createDefaultPredicateValuation());
	}

	@Override
	public void processMethodReturn() {
		scopes.pop();
	}
	
	@Override
	public void store(Scope scope) {
		if (scope instanceof FlatPredicateValuation) {
			scopes.push((FlatPredicateValuation) scope.clone());
		} else {
			throw new RuntimeException("Invalid scope type being pushed!");
		}
	}
	
	@Override
	public void restore(Scope scope) {
		if (scope instanceof FlatPredicateValuation) {
			scopes.pop();
			scopes.push((FlatPredicateValuation) scope.clone());
		} else {
			throw new RuntimeException("Invalid scope type being pushed!");
		}
	}
	
	@Override
	public FlatPredicateValuation memorize() {
		return scopes.lastElement().clone();
	}

}
