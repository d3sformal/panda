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

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

/**
 * A predicate with a constant truth value ~ false
 */
public class Contradiction extends Predicate {

    private static Contradiction instance;

    protected Contradiction() {
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void addAccessExpressionsToSet(Set<AccessExpression> out) {
    }

    @Override
    public Predicate replace(Map<AccessExpression, Expression> replacements) {
        return this;
    }

    public static Predicate create() {
        //return new Contradiction();
        if (instance == null) {
            instance = new Contradiction();
        }

        return instance;
    }

    @Override
    public Predicate update(AccessExpression expression, Expression newExpression) {
        return this;
    }

    @Override
    public Contradiction clone() {
        return this;
    }

}
