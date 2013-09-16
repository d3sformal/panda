package gov.nasa.jpf.abstraction.common.access;

import java.util.List;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.Expression;

/**
 * Access expressions:
 *  a.b.c
 *  a[i]
 *  p.q.C.d[e].f
 */
public interface AccessExpression extends Expression {	
	public List<AccessExpression> getSubAccessExpressions();
	public List<AccessExpression> getAccessExpressions();
	public AccessExpression replaceSubExpressions(Map<AccessExpression, Expression> replacements);
	
	public Root getRoot();
	public AccessExpression get(int depth);
	
	public boolean isThis();
	public boolean isStatic();
	public boolean isLocalVariable();
	public boolean isPrefixOf(AccessExpression expression);
	public boolean isSimilarToPrefixOf(AccessExpression path);
	public boolean isProperPrefixOf(AccessExpression expression);
	
	public int getLength();
	public AccessExpression cutTail();
	public AccessExpression reRoot(AccessExpression newPrefix);
	public AccessExpression reRoot(AccessExpression oldPrefix, AccessExpression newPrefix);
	
	@Override
	public abstract AccessExpression clone();
	
	public boolean isSimilarTo(AccessExpression expression);
}
