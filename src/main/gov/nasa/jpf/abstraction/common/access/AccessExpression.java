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
	public List<AccessExpression> getAccessSubExpressions();
	public List<AccessExpression> getAccessExpressions();
	public AccessExpression replaceSubExpressions(Map<AccessExpression, Expression> replacements);
	public List<AccessExpression> getAllPrefixes();
	
	public Root getRoot();
	public AccessExpression get(int depth);
	
	public boolean isThis();
	public boolean isStatic();
	public boolean isLocalVariable();
	public boolean isReturnValue();
	public boolean isPrefixOf(AccessExpression expression);
	public boolean isSimilarToPrefixOf(AccessExpression path);
	public boolean isProperPrefixOf(AccessExpression expression);
	
	public int getLength();
	public AccessExpression cutTail();
	public AccessExpression reRoot(AccessExpression newPrefix);
	public AccessExpression reRoot(AccessExpression oldPrefix, AccessExpression newPrefix);
	
	@Override
	public abstract AccessExpression clone();

    /**
     * Comparison of two access expressions ignoring indices, arrays
     */
	public boolean isSimilarTo(AccessExpression expression);
}
