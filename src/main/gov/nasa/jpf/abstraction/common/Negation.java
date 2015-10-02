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
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

/**
 * Negation of a predicate.
 *
 * Can be used to express !=, >= from = and <, and other.
 */
public class Negation extends Predicate {
    public Predicate predicate;

    protected Negation(Predicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public void addAccessExpressionsToSet(Set<AccessExpression> out) {
        predicate.addAccessExpressionsToSet(out);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Predicate replace(Map<AccessExpression, Expression> replacements) {
        Predicate newP = predicate.replace(replacements);

        if (newP == predicate) return this;
        else return create(newP);
    }

    public static Predicate create(Predicate predicate) {
        if (predicate == null) {
            return null;
        }
        if (predicate instanceof Negation) {
            return ((Negation) predicate).predicate;
        }
        if (predicate instanceof Tautology) {
            return Contradiction.create();
        }
        if (predicate instanceof Contradiction) {
            return Tautology.create();
        }

        return new Negation(predicate);
    }

    @Override
    public Predicate update(AccessExpression expression, Expression newExpression) {
        Predicate newP = predicate.update(expression, newExpression);

        if (newP == predicate) return this;
        else return create(newP);
    }

    @Override
    public Negation clone() {
        return (Negation)create(predicate);
    }
}
