package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;

public class Select extends DefaultAccessExpression implements Root {
    private AccessExpression from;
    private Expression index;
    private Integer hashCode;

    protected Select(AccessExpression from, Expression index) {
        super(from == null ? 1 : from.getLength());

        this.from = from;
        this.index = index;
    }

    // select(arr, a)
    public static Select create(AccessExpression a) {
        return new Select(null, a);
    }

    // select(a, i)
    public static Select create(AccessExpression a, Expression i) {
        return new Select(a, i);
    }

    public boolean isRoot() {
        return from == null;
    }

    public AccessExpression getFrom() {
        return from;
    }

    public Expression getIndex() {
        return index;
    }

    @Override
    public Select createShallowCopy() {
        return create(from, index);
    }

    @Override
    public Predicate getPreconditionForBeingFresh() {
        return Contradiction.create();
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean isSimilarToSlow(AccessExpression expression) {
        if (expression instanceof Select) {
            Select s = (Select) expression;

            /**
             * Distinct constant indices are not similar
             */
            if (getIndex() instanceof Constant && s.getIndex() instanceof Constant && !getIndex().equals(s.getIndex())) {
                return false;
            }

            if (isRoot()) {
                return s.isRoot();
            }

            return getFrom().isSimilarToSlow(s.getFrom());
        }

        return false;
    }

    @Override
    public boolean isEqualToSlow(AccessExpression expression) {
        if (expression instanceof Select) {
            Select s = (Select) expression;

            if (isRoot()) {
                return s.isRoot() && getIndex().equals(s.getIndex());
            }

            return getFrom().equals(s.getFrom()) && getIndex().equals(s.getIndex());
        }

        return false;
    }

    @Override
    public Select reRoot(AccessExpression newPrefix) {
        return create(newPrefix, getIndex());
    }

    @Override
    public AccessExpression cutTail() {
        if (isRoot()) {
            return this;
        }

        return getFrom();
    }

    @Override
    public AccessExpression get(int depth) {
        if (depth > getLength()) {
            return null;
        }
        if (depth == getLength()) {
            return this;
        }
        if (isRoot()) {
            return this;
        }
        return getFrom().get(depth);
    }

    @Override
    public Root getRoot() {
        if (isRoot()) {
            return this;
        }
        return getFrom().getRoot();
    }

    @Override
    public AccessExpression replaceSubExpressions(Map<AccessExpression, Expression> replacements) {
        AccessExpression newFrom = from;
        Expression newIndex = index;

        if (!isRoot()) {
            newFrom = (AccessExpression) newFrom.replace(replacements);
        }
        newIndex = newIndex.replace(replacements);

        if ((isRoot() || newFrom.equals(from)) && newIndex.equals(index)) {
            return this;
        }
        return create(newFrom, newIndex);
    }

    @Override
    public Expression update(AccessExpression expression, Expression newExpression) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public void addAccessSubExpressionsToSet(Set<AccessExpression> exprs) {
        if (!isRoot()) {
            getFrom().addAccessSubExpressionsToSet(exprs);
        }
        getIndex().addAccessExpressionsToSet(exprs);
    }

    @Override
    public String getName() {
        return "select";
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            hashCode = toString().hashCode();
        }

        return hashCode;
    }
}
