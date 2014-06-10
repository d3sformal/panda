package gov.nasa.jpf.abstraction.common;

import java.util.Set;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

/**
 * A common ancestor to all non-constant elemental predicates supported in this project
 * <, =
 *
 * For constant predicates @see gov.nasa.jpf.abstraction.common.Tautology,gov.nasa.jpf.abstraction.common.Contradiction
 * Also @see gov.nasa.jpf.abstraction.common.Negation
 */
public abstract class Comparison extends Predicate {
    public Expression a;
    public Expression b;

    protected Comparison(Expression a, Expression b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public void addAccessExpressionsToSet(Set<AccessExpression> out) {
        a.addAccessExpressionsToSet(out);
        b.addAccessExpressionsToSet(out);
    }

    /**
     * Common check for validating predicates over symbolic expressions.
     */
    protected static boolean argumentsDefined(Expression a, Expression b) {
        return a != null && b != null;
    }
}
