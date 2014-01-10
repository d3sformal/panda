package gov.nasa.jpf.abstraction.common.access.meta;

import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitable;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

/**
 * Represents a particular field (regardless of the object)
 * 
 * This is the field "f" in the expression fread(f, o)
 */
public interface Field extends PredicatesComponentVisitable, Cloneable {
	public String getName();
	public Field clone();
	public List<AccessExpression> getAccessSubExpressions();
}
