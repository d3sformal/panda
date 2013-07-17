package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

public class Implication extends Disjunction {
	
	public Predicate a;
	public Predicate b;

	public Implication(Predicate a, Predicate b) {
		super(new Negation(a), b);
		
		this.a = a;
		this.b = b;
	}
	
	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

}
