package gov.nasa.jpf.abstraction.predicate.common;

import java.util.List;

public class Context {
	public AccessPath path;
	public List<Predicate> predicates;
	
	public Context(AccessPath path, List<Predicate> predicates) {
		this.path = path;
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
