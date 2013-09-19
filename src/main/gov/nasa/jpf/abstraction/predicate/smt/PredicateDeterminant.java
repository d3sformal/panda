package gov.nasa.jpf.abstraction.predicate.smt;

import java.util.Map;

import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;

public class PredicateDeterminant {
	public Predicate positiveWeakestPrecondition;
	public Predicate negativeWeakestPrecondition;
	public Map<Predicate, TruthValue> determinants;
	
	public PredicateDeterminant(Predicate positiveWeakestPrecondition, Predicate negativeWeakestPrecondition, Map<Predicate, TruthValue> determinants) {
		this.positiveWeakestPrecondition = positiveWeakestPrecondition;
		this.negativeWeakestPrecondition = negativeWeakestPrecondition;
		this.determinants = determinants;
	}
}
