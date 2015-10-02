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
 * Disjunction represents a logical OR of two predicates. (e.g. x = 1 OR x = 2)
 */
public class Disjunction extends Formula {
    protected Disjunction(Predicate a, Predicate b) {
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
     * Method used to create disjunctions of predicates.
     * The method checks its arguments and produces a disjunction of the two predicates or an equivalent simplification with the aim to shorten SMT input and make it more readable too.
     *
     * @return Simplified formula / predicate according to (a OR true ~ true, a OR false ~ a, and other logical rules)
     */
    public static Predicate create(Predicate a, Predicate b) {
        if (!argumentsDefined(a, b)) return null;

        if (a instanceof Tautology) {
            return Tautology.create();
        }
        if (b instanceof Tautology) {
            return Tautology.create();
        }
        if (a instanceof Contradiction) {
            return b;
        }
        if (b instanceof Contradiction) {
            return a;
        }

        return new Disjunction(a, b);
    }

    @Override
    public Predicate update(AccessExpression expression, Expression newExpression) {
        Predicate newA = a.update(expression, newExpression);
        Predicate newB = b.update(expression, newExpression);

        if (newA == a && newB == b) return this;
        else return create(newA, newB);
    }

    @Override
    public Disjunction clone() {
        return (Disjunction)create(a, b);
    }

}
