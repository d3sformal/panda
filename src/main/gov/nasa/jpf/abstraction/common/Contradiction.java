package gov.nasa.jpf.abstraction.common;

import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

/**
 * A predicate with a constant truth value ~ false
 */
public class Contradiction extends Predicate {

    private static Contradiction instance;

    protected Contradiction() {
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void addAccessExpressionsToSet(Set<AccessExpression> out) {
    }

    @Override
    public Predicate replace(Map<AccessExpression, Expression> replacements) {
        return this;
    }

    public static Predicate create() {
        //return new Contradiction();
        if (instance == null) {
            instance = new Contradiction();
        }

        return instance;
    }

    @Override
    public Predicate update(AccessExpression expression, Expression newExpression) {
        return this;
    }

    @Override
    public Contradiction clone() {
        return this;
    }

}
