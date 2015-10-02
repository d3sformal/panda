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
package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;

/**
 * Implementation of unary operations regardless their concrete type.
 */
public abstract class UnaryOperatorExecutor<T> {

    final public Instruction execute(AbstractUnaryOperator<T> op, ThreadInfo ti) {
        StackFrame sf = ti.getModifiableTopFrame();

        Expression expr = getOperandExpression(sf);
        Expression result = op.getResult(expr);

        Instruction ret = op.executeConcrete(ti);

        storeExpression(result, sf);

        return ret;
    }

    protected Expression getOperandExpression(StackFrame sf, int index) {
        return ExpressionUtil.getExpression(sf.getOperandAttr(index));
    }

    abstract protected Expression getOperandExpression(StackFrame sf);
    abstract protected T getOperand(StackFrame sf);
    abstract protected void storeExpression(Expression result, StackFrame sf);
    abstract protected void storeResult(Expression result, StackFrame sf);
}
