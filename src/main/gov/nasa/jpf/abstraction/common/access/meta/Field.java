package gov.nasa.jpf.abstraction.common.access.meta;

import java.util.Set;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitable;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

/**
 * Represents a particular field (regardless of the object)
 *
 * This is the field "f" in the expression fread(f, o)
 */
public interface Field extends PredicatesComponentVisitable {
    public String getName();
    public void addAccessSubExpressionsToSet(Set<AccessExpression> out);
}
