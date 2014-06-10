package gov.nasa.jpf.abstraction.common.access.meta.impl;

import java.util.Set;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;

/**
 * The unmodified symbol "arr"
 */
public class DefaultArrays implements Arrays {
    private static DefaultArrays instance;

    public static DefaultArrays create() {
        if (instance == null) {
            instance = new DefaultArrays();
        }

        return instance;
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DefaultArrays) {
            return true;
        }

        return false;
    }

    @Override
    public void addAccessSubExpressionsToSet(Set<AccessExpression> out) {
    }
}
