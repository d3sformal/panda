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
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.Undefined;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;

/**
 * An access expression produces in circumstances where there is no other valid result
 */
public class UndefinedAccessExpression extends DefaultRoot implements Undefined {

    private static UndefinedAccessExpression instance;

    protected UndefinedAccessExpression() {
        super(null);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit((Undefined) this);
    }

    @Override
    public UndefinedAccessExpression createShallowCopy() {
        return this;
    }

    public static UndefinedAccessExpression create() {
        //return new UndefinedAccessExpression();
        if (instance == null) {
            instance = new UndefinedAccessExpression();
        }

        return instance;
    }

    @Override
    public void addAccessSubExpressionsToSet(Set<AccessExpression> out) {
    }

    @Override
    public AccessExpression replaceSubExpressions(Map<AccessExpression, Expression> replacements) {
        return this;
    }

    @Override
    public Root getRoot() {
        return this;
    }

}
