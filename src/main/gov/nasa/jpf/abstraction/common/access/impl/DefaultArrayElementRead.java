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

import gov.nasa.jpf.abstraction.common.ArrayExpression;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayAccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ArrayElementWrite;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrays;
import gov.nasa.jpf.abstraction.common.impl.NullExpression;

/**
 * Read of an array element aread(arr, a, i) ~ a[i]
 */
public class DefaultArrayElementRead extends DefaultArrayElementExpression implements ArrayElementRead {

    private Integer hashCodeValue;

    protected DefaultArrayElementRead(AccessExpression array, Expression index) {
        this(array, DefaultArrays.create(), index);
    }

    protected DefaultArrayElementRead(AccessExpression array, Arrays arrays, Expression index) {
        super(array, arrays, index);
    }

    public static DefaultArrayElementRead create(AccessExpression array, Expression index) {
        if (array == null || index == null) {
            return null;
        }

        return new DefaultArrayElementRead(array, index);
    }

    public static DefaultArrayElementRead create(AccessExpression array, Arrays arrays, Expression index) {
        if (array == null || arrays == null || index == null) {
            return null;
        }

        return new DefaultArrayElementRead(array, arrays, index);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DefaultArrayElementRead createShallowCopy() {
        return create(getArray(), getArrays(), getIndex());
    }

    @Override
    public AccessExpression reRoot(AccessExpression newPrefix) {
        return create(newPrefix, getArrays(), getIndex());
    }

    @Override
    public boolean isEqualToSlow(AccessExpression o) {
        if (o instanceof ArrayElementRead) {
            ArrayElementRead r = (ArrayElementRead) o;

            return getArrays().equals(r.getArrays()) && getArray().isEqualToSlow(r.getArray()) && getIndex().equals(r.getIndex());
        }

        return false;
    }

    @Override
    public boolean isSimilarToSlow(AccessExpression expression) {
        if (expression instanceof ArrayElementRead) {
            ArrayElementRead r = (ArrayElementRead) expression;

            /**
             * Distinct constant indices are not similar
             */
            if (getIndex() instanceof Constant && r.getIndex() instanceof Constant && !getIndex().equals(r.getIndex())) {
                return false;
            }

            return getArray().isSimilarToSlow(r.getArray());
        }

        return false;
    }

    @Override
    public int hashCode() {
        if (hashCodeValue == null) {
            hashCodeValue = ("read_element_" + getArray().hashCode() + "_" + getIndex().hashCode()).hashCode();
        }

        return hashCodeValue;
    }

    @Override
    public AccessExpression replaceSubExpressions(Map<AccessExpression, Expression> replacements) {
        AccessExpression newA = getObject().replaceSubExpressions(replacements);
        Expression newI = getIndex().replace(replacements);

        if (newA == getObject() && newI == getIndex()) return this;
        else return create(newA, getArrays(), newI);
    }


    /**
     * @see gov.nasa.jpf.abstraction.common.Predicate.update for an overall view
     */
    @Override
    public Expression update(AccessExpression expression, Expression newExpression) {
        Expression updated = getArray().update(expression, newExpression);

        // An array has been changed -> aread(awrite(...), ..., ...)
        if (expression instanceof ArrayExpression) {
            ArrayElementRead a = (ArrayElementRead) expression;

            if (updated instanceof AccessExpression) {
                AccessExpression updatedAccessExpression = (AccessExpression) updated;

                return create(updatedAccessExpression, DefaultArrayElementWrite.create(a.getArray(), a.getArrays(), a.getIndex(), newExpression), getIndex().update(expression, newExpression));
            }

            return UndefinedAccessExpression.create();
        }

        // This element is not affected by the update (a := b) so update recursively
        if (updated instanceof AccessExpression) {
            return create((AccessExpression) updated, getArrays(), getIndex().update(expression, newExpression));
        }

        // If the update causes that this expression accesses element of null
        if (updated instanceof NullExpression) {
            return NullExpression.create();
        }

        throw new RuntimeException("Unrecognized expression " + updated + "(" + updated.getClass().getName() + ")");
    }

    @Override
    public Predicate getPreconditionForBeingFresh() {
        if (getArrays() instanceof ArrayElementWrite) {
            ArrayElementWrite w = (ArrayElementWrite) getArrays();

            return Conjunction.create(Equals.create(getArray(), w.getArray()), w.getPreconditionForBeingFresh());
        }

        return Contradiction.create();
    }
}
