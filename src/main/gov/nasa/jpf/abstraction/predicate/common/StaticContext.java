package gov.nasa.jpf.abstraction.predicate.common;

import java.util.List;

/**
 * Corresponds to one static section in the input file
 * 
 * [static]
 * b = a - 1
 * a * b = 6
 * ...
 * 
 * <<< SOME OTHER SECTION OR EOF
 */
public class StaticContext extends Context {

	public StaticContext(List<Predicate> predicates) {
		super(predicates);
	}
	
	@Override
	public String toString() {
		return "[static]\n" + super.toString();
	}

}
