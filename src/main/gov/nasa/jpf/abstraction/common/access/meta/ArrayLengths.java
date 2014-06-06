package gov.nasa.jpf.abstraction.common.access.meta;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitable;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import java.util.Set;

/**
 * Representation of a global map of arrays to their length (the symbol arrlen)
 */
public interface ArrayLengths extends PredicatesComponentVisitable {
    public void addAccessSubExpressionsToSet(Set<AccessExpression> out);
}
