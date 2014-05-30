package gov.nasa.jpf.abstraction.common.access.impl;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Fresh;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Tautology;

/**
 * A special value representing a completely new object
 */
public class DefaultFresh extends DefaultRoot implements Fresh {

    private static DefaultFresh instance;

    protected DefaultFresh() {
        super("fresh");
    }

    public static DefaultFresh create() {
        //return new DefaultFresh();
        if (instance == null) {
            instance = new DefaultFresh();
        }

        return instance;
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DefaultFresh createShallowCopy() {
        return this;
    }

    @Override
    public boolean isEqualToSlow(AccessExpression o) {
        return false;
    }

    @Override
    public Predicate getPreconditionForBeingFresh() {
        return Tautology.create();
    }
}
