package gov.nasa.jpf.abstraction.predicate.smt;

import java.util.Map;

import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;

public class PredicateDeterminant {
	public Predicate weakestPrecondition;
	public Map<Predicate, TruthValue> determinants;
	
	public PredicateDeterminant(Predicate weakestPrecondition, Map<Predicate, TruthValue> determinants) {
		this.weakestPrecondition = weakestPrecondition;
		this.determinants = determinants;
	}
}
