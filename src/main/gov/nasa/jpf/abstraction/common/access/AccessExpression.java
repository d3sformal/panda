package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.Expression;
import java.util.Map;
import java.util.Set;

/**
 * Access expressions:
 *  a.b.c
 *  a[i]
 *  p.q.C.d[e].f
 */
public interface AccessExpression extends Expression {
    public void addAccessSubExpressionsToSet(Set<AccessExpression> out);

    @Override
    public void addAccessExpressionsToSet(Set<AccessExpression> out);

    public AccessExpression replaceSubExpressions(Map<AccessExpression, Expression> replacements);
    public void addAllPrefixesToSet(Set<AccessExpression> out);

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

    public AccessExpression createShallowCopy();

    /**
     * Comparison of two access expressions ignoring indices, arrays
     */
    public boolean isEqualTo(AccessExpression expression);
    public boolean isEqualToSlow(AccessExpression expression);

    public boolean isSimilarTo(AccessExpression expression);
    public boolean isSimilarToSlow(AccessExpression expression);
}
