package gov.nasa.jpf.abstraction.common.access.meta;

import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesVisitable;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

public interface ArrayLengths extends PredicatesVisitable, Cloneable {
	public ArrayLengths clone();
	public List<AccessExpression> getSubAccessExpressions();
}
