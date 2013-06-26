package gov.nasa.jpf.abstraction.predicate.common;

import java.util.List;

public class Context {
	public AccessPath path;
	public List<Predicate> predicates;
	
	public Context(AccessPath path, List<Predicate> predicates) {
		this.path = path;
		this.predicates = predicates;
	}
}
