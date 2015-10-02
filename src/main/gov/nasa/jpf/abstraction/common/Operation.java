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

import java.util.Set;

import gov.nasa.jpf.abstraction.common.Disjunction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.impl.DefaultPrimitiveExpression;

/**
 * A common ancestor for +, -, *, /, % ... (all binary operations over symbolic expressions)
 */
public abstract class Operation extends DefaultPrimitiveExpression {
    public Expression a;
    public Expression b;

    protected Operation(Expression a, Expression b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public void addAccessExpressionsToSet(Set<AccessExpression> out) {
        a.addAccessExpressionsToSet(out);
        b.addAccessExpressionsToSet(out);
    }

    protected static boolean argumentsDefined(Expression a, Expression b) {
        return a != null && b != null;
    }

    @Override
    public Predicate getPreconditionForBeingFresh() {
        return Disjunction.create(a.getPreconditionForBeingFresh(), b.getPreconditionForBeingFresh());
    }

    @Override
    public boolean equals(Object o) {
        if (getClass().equals(o.getClass())) {
            Operation op = (Operation) o;

            return a.equals(op.a) && b.equals(op.b);
        }

        return false;
    }
}
