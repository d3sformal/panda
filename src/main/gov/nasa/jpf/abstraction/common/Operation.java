package gov.nasa.jpf.abstraction.common;

import java.util.Set;

import gov.nasa.jpf.abstraction.common.Disjunction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.impl.DefaultPrimitiveExpression;

/**
 * A common ancestor for +, -, *, /, % ... (all binary operations over symbolic expressions)
 */
public abstract class Operation extends DefaultPrimitiveExpression {
    public Expression a;
    public Expression b;

    protected Operation(Expression a, Expression b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public void addAccessExpressionsToSet(Set<AccessExpression> out) {
        a.addAccessExpressionsToSet(out);
        b.addAccessExpressionsToSet(out);
    }

    protected static boolean argumentsDefined(Expression a, Expression b) {
        return a != null && b != null;
    }

    @Override
    public Predicate getPreconditionForBeingFresh() {
        return Disjunction.create(a.getPreconditionForBeingFresh(), b.getPreconditionForBeingFresh());
    }
}
