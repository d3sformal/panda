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
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

/**
 * Implication between two predicates / formulas over symbolic expressions. (e.g. (x = 0 AND y = x) IMPLIES y = 0)
 */
public class Implication extends Disjunction {

    public Predicate a;
    public Predicate b;

    protected Implication(Predicate a, Predicate b) {
        super(Negation.create(a), b);

        this.a = a;
        this.b = b;
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    public static Predicate create(Predicate a, Predicate b) {
        if (!argumentsDefined(a, b)) return null;

        if (a instanceof Tautology) {
            return b;
        }
        if (a instanceof Contradiction) {
            return Tautology.create();
        }
        if (b instanceof Tautology) {
            return Tautology.create();
        }
        if (b instanceof Contradiction) {
            return Negation.create(a);
        }

        return new Implication(a, b);
    }

    @Override
    public Predicate replace(Map<AccessExpression, Expression> replacements) {
        Predicate newA = a.replace(replacements);
        Predicate newB = b.replace(replacements);

        if (newA == a && newB == b) return this;
        else return create(newA, newB);
    }

    @Override
    public Predicate update(AccessExpression expression, Expression newExpression) {
        Predicate newA = a.update(expression, newExpression);
        Predicate newB = b.update(expression, newExpression);

        if (newA == a && newB == b) return this;
        else return create(newA, newB);
    }

}
