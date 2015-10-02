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

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

/**
 * Conjunction represents a logical AND of two predicates. (e.g. x > 0 AND x < 10)
 */
public class Conjunction extends Formula {

    protected Conjunction(Predicate a, Predicate b) {
        super(a, b);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Predicate replace(Map<AccessExpression, Expression> replacements) {
        Predicate newA = a.replace(replacements);
        Predicate newB = b.replace(replacements);

        if (newA == a && newB == b) return this;
        else return create(newA, newB);
    }

    /**
     * Method used to create conjunctions of predicates.
     * The method checks its arguments and produces a conjunction of the two predicates or an equivalent simplification with the aim to shorten SMT input and make it more readable too.
     *
     * @return Simplified formula / predicate according to (a AND true ~ a, a AND false ~ false, and other logical rules)
     */
    public static Predicate create(Predicate a, Predicate b) {
        if (!argumentsDefined(a, b)) return null;

        if (a instanceof Tautology) {
            return b;
        }
        if (b instanceof Tautology) {
            return a;
        }
        if (a instanceof Contradiction) {
            return Contradiction.create();
        }
        if (b instanceof Contradiction) {
            return Contradiction.create();
        }

        return new Conjunction(a, b);
    }

    @Override
    public Predicate update(AccessExpression expression, Expression newExpression) {
        Predicate newA = a.update(expression, newExpression);
        Predicate newB = b.update(expression, newExpression);

        if (newA == a && newB == b) return this;
        else return create(newA, newB);
    }

    @Override
    public Conjunction clone() {
        return (Conjunction)create(a, b);
    }

}
