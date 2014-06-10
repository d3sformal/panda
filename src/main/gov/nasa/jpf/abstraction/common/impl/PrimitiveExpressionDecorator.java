package gov.nasa.jpf.abstraction.common.impl;

import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.PrimitiveExpression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ReturnValue;
import gov.nasa.jpf.abstraction.predicate.state.SymbolTable;

/**
 * Wrapper used for marking expressions as primitive
 */
public class PrimitiveExpressionDecorator extends DefaultPrimitiveExpression {
    private Expression expression;

    public PrimitiveExpressionDecorator(Expression expression) {
        this.expression = expression;
    }

    public static PrimitiveExpression wrap(Expression expression, SymbolTable symbols) {
        if (expression instanceof PrimitiveExpression) {
            return (PrimitiveExpression) expression;
        }

        if (expression instanceof ReturnValue) {
            ReturnValue r = (ReturnValue) expression;

            if (!r.isReference()) {
                return PrimitiveExpressionDecorator.create(expression);
            }
        }
        if (expression instanceof AccessExpression) {
            AccessExpression path = (AccessExpression) expression;

            if (symbols.isPrimitive(path)) {
                return PrimitiveExpressionDecorator.create(expression);
            }
        }

        throw new RuntimeException("Invalid cast to Primitive Expression " + expression + " " + (expression == null ? "null" : expression.getClass().getSimpleName()));
    }

    private static PrimitiveExpressionDecorator create(Expression expression) {
        if (expression == null) {
            return null;
        }

        return new PrimitiveExpressionDecorator(expression);
    }

    @Override
    public void addAccessExpressionsToSet(Set<AccessExpression> out) {
        expression.addAccessExpressionsToSet(out);
    }

    @Override
    public Expression replace(Map<AccessExpression, Expression> replacements) {
        Expression newE = this.expression.replace(replacements);

        if (newE == this.expression) return this;
        else return create(newE);
    }

    @Override
    public Expression update(AccessExpression expression, Expression newExpression) {
        Expression newE = this.expression.update(expression, newExpression);

        if (newE == this.expression) return this;
        else return create(newE);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        expression.accept(visitor);
    }

    @Override
    public Predicate getPreconditionForBeingFresh() {
        return expression.getPreconditionForBeingFresh();
    }

    @Override
    public boolean equals(Object o) {
        return expression.equals(o);
    }

    @Override
    public int hashCode() {
        return expression.hashCode();
    }

}
