package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import java.util.Map;

/**
 * Subtract represents an arithmetic difference of two variables (e.g. a - b)
 */
public class Subtract extends Operation {
    protected Subtract(Expression a, Expression b) {
        super(a, b);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Subtract replace(Map<AccessExpression, Expression> replacements) {
        Expression newA = a.replace(replacements);
        Expression newB = b.replace(replacements);

        if (newA == a && newB == b) return this;
        else return new Subtract(newA, newB);
    }

    /**
     * Checked creation of the symbolic expression.
     *
     * No matter the validity of the arguments (e.g. being null) this method is responsible for coping with it
     *
     * @param a left hand side operand
     * @param b right hand side operand
     * @return symbolic expression representing the difference / undefined expression / null
     */
    public static Operation create(Expression a, Expression b) {
        if (!argumentsDefined(a, b)) return null;

        if (a instanceof Undefined) return UndefinedOperationResult.create();
        if (b instanceof Undefined) return UndefinedOperationResult.create();

        return new Subtract(a, b);
    }

    @Override
    public Expression update(AccessExpression expression, Expression newExpression) {
        Expression newA = a.update(expression, newExpression);
        Expression newB = b.update(expression, newExpression);

        if (newA == a && newB == b) return this;
        else return create(newA, newB);
    }
}
