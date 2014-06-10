package gov.nasa.jpf.abstraction.common.access.meta;

import java.util.Set;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitable;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

/**
 * Represents a set of all arrays (the symbol arr)
 */
public interface Arrays extends PredicatesComponentVisitable {
    public void addAccessSubExpressionsToSet(Set<AccessExpression> out);
}
