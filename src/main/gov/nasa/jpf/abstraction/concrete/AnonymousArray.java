package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.abstraction.common.ArrayExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;

/**
 * A freshly allocated array (or a duplicate) --- not loaded from a variable.
 */
public class AnonymousArray extends AnonymousObject implements ArrayExpression {

    private Expression length;

    protected AnonymousArray(Reference reference, Expression length, boolean duplicate) {
        super(reference, duplicate);

        this.length = length;
    }

    public Expression getArrayLength() {
        return length;
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public AnonymousArray createShallowCopy() {
        return this;
    }

    @Override
    public boolean isSimilarToSlow(AccessExpression expression) {
        if (expression instanceof AnonymousArray) {
            AnonymousArray o = (AnonymousArray) expression;

            return getReference().equals(o.getReference());
        }

        return false;
    }

    public static AnonymousArray create(Reference reference, Expression length, boolean duplicate) {
        if (reference == null) {
            return null;
        }

        return new AnonymousArray(reference, length, duplicate);
    }

    public static AnonymousArray create(Reference reference, Expression length) {
        return create(reference, length, false);
    }

}
