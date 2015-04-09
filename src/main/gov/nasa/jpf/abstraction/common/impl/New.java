package gov.nasa.jpf.abstraction.common.impl;

import java.util.Set;

import gov.nasa.jpf.abstraction.common.Assign;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicateNotCloneableException;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;

public class New extends Assign {

    public AnonymousObject object;

    private New(AnonymousObject object) {
        this.object = object;
    }

    @Override
    public void addAccessExpressionsToSet(Set<AccessExpression> out) {
        object.addAccessExpressionsToSet(out);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    public static Predicate create(AnonymousObject object) {
        return new New(object);
    }

    @Override
    public New clone() {
        throw new PredicateNotCloneableException("Should not be copying this");
    }
}
