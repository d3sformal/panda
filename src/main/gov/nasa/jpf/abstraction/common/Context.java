package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.PredicatesVisitable;

import java.util.List;

public abstract class Context implements PredicatesVisitable {
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
