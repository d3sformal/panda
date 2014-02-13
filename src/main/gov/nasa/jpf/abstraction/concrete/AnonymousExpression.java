package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;

/**
 * Interface of all Objects / Arrays obtained by allocation (or duplicates of such)
 */
public interface AnonymousExpression extends Expression {
	public Reference getReference();
}
