package gov.nasa.jpf.abstraction.predicate.common;

import java.util.List;

public class GlobalContext extends Context {

	public GlobalContext(List<Predicate> predicates) {
		super(predicates);
	}
	
	@Override
	public String toString() {
		return "[global]\n" + super.toString();
	}

}
