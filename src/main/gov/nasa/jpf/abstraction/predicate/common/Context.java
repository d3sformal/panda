package gov.nasa.jpf.abstraction.predicate.common;

import java.util.List;

public class Context {
	public List<Predicate> predicates;
	
	public Context(List<Predicate> predicates) {
		this.predicates = predicates;
	}
	
	@Override
	public String toString() {
		String ret = "";

		for (Predicate p : predicates) {
			ret += p.toString() + "\n";
		}
		
		return ret;
	}
}
