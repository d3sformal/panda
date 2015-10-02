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
package gov.nasa.jpf.abstraction.common.impl;

import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.ObjectExpression;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.state.universe.Universe;

public class NullExpression extends Constant implements ObjectExpression {

    private static NullExpression instance;

    protected NullExpression() {
        super(Universe.NULL);
    }

    @Override
    public void addAccessExpressionsToSet(Set<AccessExpression> out) {
    }

    @Override
    public NullExpression replace(Map<AccessExpression, Expression> replacements) {
        return this;
    }

    @Override
    public NullExpression update(AccessExpression expression, Expression newExpression) {
        return this;
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    public static NullExpression create() {
        if (instance == null) {
            instance = new NullExpression();
        }

        return instance;
    }

    @Override
    public Predicate getPreconditionForBeingFresh() {
        return Contradiction.create();
    }
}
