package gov.nasa.jpf.abstraction.common;

import java.util.Map;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.Undefined;

/**
 * Predicate on inequality of two symbolic expressions. (e.g. x < y)
 */
public class LessThan extends Comparison {
    protected LessThan(Expression a, Expression b) {
        super(a, b);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Predicate replace(Map<AccessExpression, Expression> replacements) {
        Expression newA = a.replace(replacements);
        Expression newB = b.replace(replacements);

        if (newA == a && newB == b) return this;
        else return create(newA, newB);
    }

    public static Predicate create(Expression a, Expression b) {
        if (!argumentsDefined(a, b)) return null;

        if (a instanceof Undefined) return Contradiction.create();
        if (b instanceof Undefined) return Contradiction.create();

        return new LessThan(a, b);
    }

    @Override
    public Predicate update(AccessExpression expression, Expression newExpression) {
        Expression newA = a.update(expression, newExpression);
        Expression newB = b.update(expression, newExpression);

        if (newA == a && newB == b) return this;
        else return create(newA, newB);
    }
}
