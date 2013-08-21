package gov.nasa.jpf.abstraction.common.access.meta;

import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesVisitable;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

public interface Arrays extends PredicatesVisitable, Cloneable {
	public Arrays clone();
	public List<AccessExpression> getSubAccessExpressions();
}
