package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.predicate.common.AccessPath;
import gov.nasa.jpf.abstraction.predicate.common.Context;
import gov.nasa.jpf.abstraction.predicate.common.Expression;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.common.Predicates;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

public class ScopedPredicateValuation implements PredicateValuation, Scoped {
	private Stack<FlatPredicateValuation> scopes = new Stack<FlatPredicateValuation>();
	private Predicates predicateSet;
	
	public ScopedPredicateValuation(Predicates predicateSet) {
		this.predicateSet = predicateSet;
	}
	
	@Override
	public FlatPredicateValuation createDefaultScope() {
		FlatPredicateValuation valuation = new FlatPredicateValuation();
		
		for (Context context : predicateSet.contexts) {
			for (Predicate predicate : context.predicates) {
				valuation.put(predicate, TruthValue.UNDEFINED);
			}
		}
		
		return valuation;
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
		scopes.push(createDefaultScope());
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
	
	@Override
	public String toString() {
		return scopes.lastElement().toString();
	}

	@Override
	public void reevaluate(Set<AccessPath> affected, Expression expression) {
		scopes.lastElement().reevaluate(affected, expression);
	}
	
	@Override
	public int count() {
		return scopes.size();
	}

}
