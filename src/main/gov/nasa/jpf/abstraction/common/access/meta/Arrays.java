package gov.nasa.jpf.abstraction.common.access.meta;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitable;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import java.util.Set;

/**
 * Represents a set of all arrays (the symbol arr)
 */
public interface Arrays extends PredicatesComponentVisitable {
    public void addAccessSubExpressionsToSet(Set<AccessExpression> out);
}
