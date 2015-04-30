package gov.nasa.jpf.abstraction.common;

import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;

public class IfThenElse implements Expression {
    public Predicate cond;
    public Expression a;
    public Expression b;

    private IfThenElse(Expression cond, Expression a, Expression b) {
        this(Negation.create(Equals.create(cond, Constant.create(0))), a, b);
    }

    private IfThenElse(Predicate cond, Expression a, Expression b) {
        this.cond = cond;
        this.a = a;
        this.b = b;
    }

    @Override
    public void addAccessExpressionsToSet(Set<AccessExpression> out) {
        cond.addAccessExpressionsToSet(out);
        a.addAccessExpressionsToSet(out);
        b.addAccessExpressionsToSet(out);
    }

    @Override
    public Expression replace(Map<AccessExpression, Expression> replacements) {
        Predicate cond = this.cond.replace(replacements);
        Expression a = this.a.replace(replacements);
        Expression b = this.b.replace(replacements);

        if (cond == this.cond && a == this.a && b == this.b) {
            return this;
        }

        return create(cond, a, b);
    }

    @Override
    public Expression replace(AccessExpression expression, Expression newExpression) {
        Predicate cond = this.cond.replace(expression, newExpression);
        Expression a = this.a.replace(expression, newExpression);
        Expression b = this.b.replace(expression, newExpression);

        if (cond == this.cond && a == this.a && b == this.b) {
            return this;
        }

        return create(cond, a, b);
    }

    @Override
    public String toString(Notation policy) {
        return Notation.convertToString(this, policy);
    }

    @Override
    public Expression update(AccessExpression expression, Expression newExpression) {
        Predicate cond = this.cond.update(expression, newExpression);
        Expression a = this.a.update(expression, newExpression);
        Expression b = this.b.update(expression, newExpression);

        if (cond == this.cond && a == this.a && b == this.b) {
            return this;
        }

        return create(cond, a, b);
    }

    @Override
    public Predicate getPreconditionForBeingFresh() {
        return Disjunction.create(Conjunction.create(cond, a.getPreconditionForBeingFresh()), Conjunction.create(Negation.create(cond), b.getPreconditionForBeingFresh()));
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof IfThenElse) {
            IfThenElse ite = (IfThenElse) o;

            return cond.equals(ite.cond) && a.equals(ite.a) && b.equals(ite.b);
        }

        return false;
    }

    public static IfThenElse create(Predicate cond, Expression a, Expression b) {
        return new IfThenElse(cond, a, b);
    }

    public static IfThenElse create(Expression cond, Expression a, Expression b) {
        return new IfThenElse(cond, a, b);
    }
}
