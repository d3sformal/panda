package gov.nasa.jpf.abstraction.common.access.impl;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectAccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;
import java.util.Set;

/**
 * Expressions accessing objects (fields, array elements, array length):
 *
 * o.f
 * a[i]
 * a.length
 */
public abstract class DefaultObjectAccessExpression extends DefaultAccessExpression implements ObjectAccessExpression {

    protected Root root;
    private AccessExpression expression;

    protected DefaultObjectAccessExpression(AccessExpression expression) {
        super(expression.getLength() + 1);

        this.root = expression.getRoot();
        this.expression = expression;
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        expression.accept(visitor);
    }

    @Override
    public final AccessExpression cutTail() {
        return getObject();
    }

    @Override
    public void setObject(AccessExpression expression) {
        this.expression = expression;
    }

    @Override
    public AccessExpression getObject() {
        return expression;
    }

    @Override
    public final AccessExpression get(int depth) {
        if (depth > getLength()) {
            return null;
        }
        if (depth == getLength()) {
            return this;
        }

        return getObject().get(depth);
    }

    @Override
    public abstract DefaultObjectAccessExpression createShallowCopy();

    @Override
    public void addAccessSubExpressionsToSet(Set<AccessExpression> out) {
        getObject().addAccessSubExpressionsToSet(out);
    }

    @Override
    public final Root getRoot() {
        return root;
    }
}
