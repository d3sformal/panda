package gov.nasa.jpf.abstraction.common.access;

import java.util.List;

import gov.nasa.jpf.abstraction.common.Expression;

public interface AccessExpression extends Expression {	
	public List<AccessExpression> getSubAccessExpressions();
	public List<AccessExpression> getAccessExpressions();
	public AccessExpression replaceSubExpressions(AccessExpression expression, Expression newExpression);
	
	public Root getRoot();
	public AccessExpression getTail();
	public AccessExpression get(int depth);
	
	public boolean isPrefixOf(AccessExpression expression);
	public boolean isSimilarToPrefixOf(AccessExpression path);
	public boolean isProperPrefixOf(AccessExpression expression);
	
	public int getLength();
	public AccessExpression cutTail();
	public AccessExpression reRoot(AccessExpression newPrefix);
	public AccessExpression reRoot(AccessExpression oldPrefix, AccessExpression newPrefix);
	
	@Override
	public abstract AccessExpression clone();
}
