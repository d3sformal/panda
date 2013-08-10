package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;

import java.util.List;

public interface Expression extends PredicatesVisitable, Cloneable {	
	public List<AccessExpression> getAccessExpressions();
	public Expression replace(AccessExpression expression, Expression newExpression);
    public String toString(NotationPolicy policy);
    public Expression clone();
    public Expression update(AccessExpression expression, Expression newExpression);
}