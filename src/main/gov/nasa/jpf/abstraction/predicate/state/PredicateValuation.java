package gov.nasa.jpf.abstraction.predicate.state;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;

public interface PredicateValuation {
	public void put(Predicate predicate, TruthValue value);
	public TruthValue get(Predicate predicate);
	public Iterator<Map.Entry<Predicate, TruthValue>> iterator();
	public void reevaluate(AccessPath affected, Set<AccessPath> resolvedAffected, Expression expression);
	public TruthValue evaluate(Predicate predicate);
}
