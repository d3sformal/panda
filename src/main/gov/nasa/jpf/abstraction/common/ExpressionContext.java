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

import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitable;

/**
 * Context is a container holding expressions that are targeted at a specific runtime scope (static, object, method)
 *
 * @see gov.nasa.jpf.abstraction.common.StaticExpressionContext for a container of expressions over static fields
 * @see gov.nasa.jpf.abstraction.common.ObjectExpressionContext for a container of expressions over static fields, instance fields
 * @see gov.nasa.jpf.abstraction.common.MethodExpressionContext for a container of expressions over static fields, instance fields, local variables (including method parameters)
 */
public abstract class ExpressionContext implements PredicatesComponentVisitable {
    public List<Expression> expressions;

    public ExpressionContext(List<Expression> expressions) {
        this.expressions = expressions;
    }

    public abstract PredicateContext getPredicateContextOfProperType();

    @Override
    public String toString() {
        String ret = "";

        for (Expression e : expressions) {
            ret += e.toString() + "\n";
        }

        return ret;
    }
}
