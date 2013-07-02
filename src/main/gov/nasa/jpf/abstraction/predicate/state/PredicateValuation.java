package gov.nasa.jpf.abstraction.predicate.state;

import java.util.Iterator;
import java.util.Map;

import gov.nasa.jpf.abstraction.predicate.grammar.Predicate;

public interface PredicateValuation {
	public void put(Predicate predicate, TruthValue value);
	public TruthValue get(Predicate predicate);
	public Iterator<Map.Entry<Predicate, TruthValue>> iterator();
}
