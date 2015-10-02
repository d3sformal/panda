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

import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Fresh;

/**
 * A special value representing a completely new object
 */
public class DefaultFresh extends DefaultRoot implements Fresh {

    private static DefaultFresh instance;

    protected DefaultFresh() {
        super("fresh");
    }

    public static DefaultFresh create() {
        //return new DefaultFresh();
        if (instance == null) {
            instance = new DefaultFresh();
        }

        return instance;
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DefaultFresh createShallowCopy() {
        return this;
    }

    @Override
    public boolean isEqualToSlow(AccessExpression o) {
        return false;
    }

    @Override
    public Predicate getPreconditionForBeingFresh() {
        return Tautology.create();
    }
}
