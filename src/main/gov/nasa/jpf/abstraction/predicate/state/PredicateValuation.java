package gov.nasa.jpf.abstraction.predicate.state;

import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;

public interface PredicateValuation extends Iterable<Map.Entry<Predicate, TruthValue>> {
	public void put(Predicate predicate, TruthValue value);
	public boolean containsKey(Predicate predicate);
	public TruthValue get(Predicate predicate);
	public Set<Predicate> getPredicates();
	public void reevaluate(AccessExpression affected, Set<AccessExpression> resolvedAffected, Expression expression);
	public TruthValue evaluatePredicate(Predicate predicate);
}
