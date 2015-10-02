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

import gov.nasa.jpf.vm.StackFrame;

import gov.nasa.jpf.abstraction.common.Expression;

/**
 * Implements type specific parts of the operation ( @see gov.nasa.jpf.abstraction.bytecode.BinaryOperatorExecutor )
 *   - layout of operands (and their attributes) on the stack (type size comes into play)
 */
public class LongIntegerBinaryOperatorExecutor extends BinaryOperatorExecutor<Long, Integer> {

    private static LongIntegerBinaryOperatorExecutor instance;

    public static LongIntegerBinaryOperatorExecutor getInstance() {
        if (instance == null) {
            instance = new LongIntegerBinaryOperatorExecutor();
        }

        return instance;
    }

    @Override
    protected Expression getFirstOperandExpression(StackFrame sf) {
        return getOperandExpression(sf, 2);
    }

    @Override
    protected Expression getSecondOperandExpression(StackFrame sf) {
        return getOperandExpression(sf, 0);
    }

    @Override
    protected Long getFirstOperand(StackFrame sf) {
        return sf.peekLong(1);
    }

    @Override
    protected Integer getSecondOperand(StackFrame sf) {
        return sf.peek(0);
    }

    @Override
    final protected void storeExpression(Expression result, StackFrame sf) {
        sf.setLongOperandAttr(result);
    }

}
