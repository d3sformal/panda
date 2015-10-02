/*
 * Copyright (C) 2015, Charles University in Prague.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthWrite;
import gov.nasa.jpf.abstraction.common.access.meta.ArrayLengths;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrayLengths;

/**
 * Write to an array length: alengthupdate(arrlen, a, l) ~ a := new int[l]
 */
public class DefaultArrayLengthWrite extends DefaultArrayLengthExpression implements ArrayLengthWrite {

    private Expression newValue;
    private Integer hashCodeValue;

    protected DefaultArrayLengthWrite(AccessExpression array, Expression newValue) {
        this(array, DefaultArrayLengths.create(), newValue);
    }

    protected DefaultArrayLengthWrite(AccessExpression array, ArrayLengths arrayLengths, Expression newValue) {
        super(array, arrayLengths);

        this.newValue = newValue;
    }

    @Override
    public Expression getNewValue() {
        return newValue;
    }

    public static DefaultArrayLengthWrite create(AccessExpression array, Expression newValue) {
        if (array == null || newValue == null) {
            return null;
        }

        return new DefaultArrayLengthWrite(array, newValue);
    }

    public static DefaultArrayLengthWrite create(AccessExpression array, ArrayLengths arrayLengths, Expression newValue) {
        if (array == null || arrayLengths == null || newValue == null) {
            return null;
        }

        return new DefaultArrayLengthWrite(array, arrayLengths, newValue);
    }

    @Override
    public void addAccessSubExpressionsToSet(Set<AccessExpression> out) {
        super.addAccessSubExpressionsToSet(out);

        newValue.addAccessExpressionsToSet(out);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DefaultArrayLengthWrite createShallowCopy() {
        return create(getArray(), getArrayLengths(), getNewValue());
    }

    @Override
    public AccessExpression reRoot(AccessExpression newPrefix) {
        return create(newPrefix, getArrayLengths(), getNewValue());
    }

    @Override
    public boolean isEqualToSlow(AccessExpression o) {
        if (o instanceof ArrayLengthWrite) {
            ArrayLengthWrite w = (ArrayLengthWrite) o;

            return getArrayLengths().equals(w.getArrayLengths()) && getArray().isEqualToSlow(w.getArray()) && getNewValue().equals(w.getNewValue());
        }

        return false;
    }

    @Override
    public boolean isSimilarToSlow(AccessExpression expression) {
        if (expression instanceof ArrayLengthWrite) {
            ArrayLengthWrite w = (ArrayLengthWrite) expression;

            return getArray().isSimilarToSlow(w.getArray());
        }

        return false;
    }

    @Override
    public int hashCode() {
        if (hashCodeValue == null) {
            hashCodeValue = ("write_length_" + getObject().hashCode() + "_" + getNewValue().hashCode()).hashCode();
        }

        return hashCodeValue;
    }

    @Override
    public AccessExpression replaceSubExpressions(Map<AccessExpression, Expression> replacements) {
        AccessExpression newA = getObject().replaceSubExpressions(replacements);
        Expression newNV = getNewValue().replace(replacements);

        if (newA == getObject() && newNV == getNewValue()) return this;
        else return create(newA, getArrayLengths(), newNV);
    }

    @Override
    public Predicate getPreconditionForBeingFresh() {
        return getNewValue().getPreconditionForBeingFresh();
    }

}
