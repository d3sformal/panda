package gov.nasa.jpf.abstraction.common.access.meta.impl;

import java.util.Set;

import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.meta.ArrayLengths;

/**
 * The unmodified symbol "arrlen"
 */
public class DefaultArrayLengths implements ArrayLengths {
    private static DefaultArrayLengths instance;

    public static DefaultArrayLengths create() {
        if (instance == null) {
            instance = new DefaultArrayLengths();
        }

        return instance;
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DefaultArrayLengths) {
            return true;
        }

        return false;
    }

    @Override
    public void addAccessSubExpressionsToSet(Set<AccessExpression> out) {
    }

    @Override
    public String toString() {
        return Notation.convertToString(this);
    }
}
