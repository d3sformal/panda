package gov.nasa.jpf.abstraction.common.impl;

import java.util.Set;

import gov.nasa.jpf.abstraction.common.Assign;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementWrite;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;

public class ArraysAssign extends Assign {

    public Arrays arrays;
    public Arrays newArrays;

    private ArraysAssign(Arrays arrays, Arrays newArrays) {
        this.arrays = arrays;
        this.newArrays = newArrays;
    }

    @Override
    public void addAccessExpressionsToSet(Set<AccessExpression> out) {
        if (newArrays instanceof ArrayElementWrite) {
            ((ArrayElementWrite) newArrays).addAccessExpressionsToSet(out);
        }
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    public static Predicate create(Arrays arrays, Arrays newArrays) {
        return new ArraysAssign(arrays, newArrays);
    }
}
