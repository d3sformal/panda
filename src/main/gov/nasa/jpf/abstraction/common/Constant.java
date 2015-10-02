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
package gov.nasa.jpf.abstraction.common;

import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.impl.DefaultExpression;

/**
 * A common class for symbolic expressions of all the numerical constants present in the execution
 */
public class Constant extends DefaultExpression implements PrimitiveExpression {
    public Number value;

    protected Constant(Number value) {
        this.value = value;
    }

    @Override
    public void addAccessExpressionsToSet(Set<AccessExpression> out) {
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Constant replace(Map<AccessExpression, Expression> replacements) {
        return this;
    }

    public static Constant create(int value) {
        return new Constant(value);
    }

    public static Constant create(float value) {
        return new Constant(value);
    }

    public static Constant create(long value) {
        return new Constant(value);
    }

    public static Constant create(double value) {
        return new Constant(value);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Constant) {
            Constant c = (Constant) o;

            return value.equals(c.value);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public Constant update(AccessExpression expression, Expression newExpression) {
        return this;
    }

    @Override
    public Predicate getPreconditionForBeingFresh() {
        return Contradiction.create();
    }
}
