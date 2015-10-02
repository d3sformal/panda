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

import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.PandaConfig;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.smt.SMT;

/**
 * Divide represents division of two variables (e.g. a / b)
 */
public class Divide extends Operation {
    protected Divide(Expression a, Expression b) {
        super(a, b);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Divide replace(Map<AccessExpression, Expression> replacements) {
        Expression newA = a.replace(replacements);
        Expression newB = b.replace(replacements);

        if (newA == a && newB == b) return this;
        else return new Divide(newA, newB);
    }

    /**
     * Checked creation of the symbolic expression.
     *
     * No matter the validity of the arguments (e.g. being null) this method is responsible for coping with it
     *
     * @param a left hand side operand
     * @param b right hand side operand
     * @return symbolic expression representing the division / undefined expression / null
     */
    public static Expression create(Expression a, Expression b) {
        Expression min = createMinimized(a, b);

        if (VM.getVM() != null && PandaConfig.getInstance().checkLanguageMinimization()) {
            Expression raw = new Divide(a, b);

            if (!SMT.checkEquivalence(min, raw)) {
                throw new RuntimeException("Wrong convertion from: " + raw + " to: " + min);
            }
        }

        return min;
    }

    public static Expression createMinimized(Expression a, Expression b) {
        if (!argumentsDefined(a, b)) return null;

        if (a instanceof Undefined) return UndefinedOperationResult.create();
        if (b instanceof Undefined) return UndefinedOperationResult.create();

        if (a instanceof Constant && b instanceof Constant) {
            Constant c = (Constant) a;
            Constant d = (Constant) b;

            return new Constant(c.value.intValue() / d.value.intValue());
        }

        return new Divide(a, b);
    }

    @Override
    public Expression update(AccessExpression expression, Expression newExpression) {
        Expression newA = a.update(expression, newExpression);
        Expression newB = b.update(expression, newExpression);

        if (newA == a && newB == b) return this;
        else return create(newA, newB);
    }
}
