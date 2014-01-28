package gov.nasa.jpf.abstraction.common.access.meta;

import java.util.Set;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitable;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

/**
 * Representation of a global map of arrays to their length (the symbol arrlen)
 */
public interface ArrayLengths extends PredicatesComponentVisitable, Cloneable {
	public ArrayLengths clone();
	public void addAccessSubExpressionsToSet(Set<AccessExpression> out);
}
