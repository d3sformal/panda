package gov.nasa.jpf.abstraction.predicate;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.predicate.common.Predicates;

public class PredicateAbstraction extends Abstraction {
	private Predicates predicateSet;
	
	public PredicateAbstraction(Predicates predicateSet) {
		this.predicateSet = predicateSet;
	}
}
