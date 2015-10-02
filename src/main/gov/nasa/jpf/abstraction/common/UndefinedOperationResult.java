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

import gov.nasa.jpf.abstraction.common.access.AccessExpression;

/**
 * A concrete instance of an undefined result
 */
public class UndefinedOperationResult extends Operation implements Undefined {

    private static UndefinedOperationResult instance;

    protected UndefinedOperationResult() {
        super(null, null);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    public static UndefinedOperationResult create() {
        //return new UndefinedOperationResult();
        if (instance == null) {
            instance = new UndefinedOperationResult();
        }

        return instance;
    }

    @Override
    public Expression replace(Map<AccessExpression, Expression> replacements) {
        return this;
    }

    @Override
    public Expression update(AccessExpression expression, Expression newExpression) {
        return this;
    }

}
