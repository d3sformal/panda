package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

import java.util.List;

/**
 * Corresponds to one static section in the input file
 * 
 * [static]
 * b = a - 1
 * a * b = 6
 * ...
 * 
 * <<< SOME OTHER SECTION OR EOF (End of File)
 * 
 * @see gov.nasa.jpf.abstraction.predicate.grammar (grammar file Predicates.g4)
 */
public class StaticContext extends Context {

	public StaticContext(List<Predicate> predicates) {
		super(predicates);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}
}
