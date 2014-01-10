package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitable;

import java.util.List;

/**
 * Context is a container holding predicates that are targeted at a specific runtime scope (static, object, method)
 * 
 * @see gov.nasa.jpf.abstraction.common.StaticContext for a container of predicates over static fields
 * @see gov.nasa.jpf.abstraction.common.ObjectContext for a container of predicates over static fields, instance fields
 * @see gov.nasa.jpf.abstraction.common.MethodContext for a container of predicates over static fields, instance fields, local variables (including method parameters)
 */
public abstract class Context implements PredicatesComponentVisitable {
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
