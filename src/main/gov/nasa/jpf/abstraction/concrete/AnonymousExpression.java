package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;

/**
 * Interface of all Objects / Arrays obtained by allocation (or duplicates of such)
 */
public interface AnonymousExpression {
	public Reference getReference();
}
