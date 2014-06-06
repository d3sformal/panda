package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.impl.DefaultExpression;
import java.util.Map;
import java.util.Set;

/**
 * A dummy expression (may be used when there would be no expression = null)
 */
public class EmptyExpression extends DefaultExpression {

    private static EmptyExpression instance;

    protected EmptyExpression() {
    }

    @Override
    public void addAccessExpressionsToSet(Set<AccessExpression> out) {
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    public static EmptyExpression create() {
        //return new EmptyExpression();
        if (instance == null) {
            instance = new EmptyExpression();
        }

        return instance;
    }

    @Override
    public Expression update(AccessExpression expression, Expression newExpression) {
        return this;
    }

    @Override
    public Predicate getPreconditionForBeingFresh() {
        return Contradiction.create();
    }

    @Override
    public Expression replace(Map<AccessExpression, Expression> replacements) {
        return this;
    }

}
