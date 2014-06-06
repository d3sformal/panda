package gov.nasa.jpf.abstraction.common.access.meta;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitable;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import java.util.Set;

/**
 * Represents a particular field (regardless of the object)
 *
 * This is the field "f" in the expression fread(f, o)
 */
public interface Field extends PredicatesComponentVisitable {
    public String getName();
    public void addAccessSubExpressionsToSet(Set<AccessExpression> out);
}
