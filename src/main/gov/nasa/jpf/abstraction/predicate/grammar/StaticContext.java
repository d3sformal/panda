package gov.nasa.jpf.abstraction.predicate.grammar;

import java.util.List;

public class StaticContext extends Context {

	public StaticContext(List<Predicate> predicates) {
		super(predicates);
	}
	
	@Override
	public String toString() {
		return "[static]\n" + super.toString();
	}

}
