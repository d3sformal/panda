package gov.nasa.jpf.abstraction.common.access.meta;

import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesVisitable;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

public interface Field extends PredicatesVisitable, Cloneable {
	public String getName();
	public Field clone();
	public List<AccessExpression> getSubAccessExpressions();
}
