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
 * Implements type specific parts of the operation ( @see gov.nasa.jpf.abstraction.bytecode.UnaryOperatorExecutor )
 *   - layout of the stack (type size comes into play)
 */
public class FloatUnaryOperatorExecutor extends UnaryOperatorExecutor<Float> {

    private static FloatUnaryOperatorExecutor instance;

    public static FloatUnaryOperatorExecutor getInstance() {
        if (instance == null) {
            instance = new FloatUnaryOperatorExecutor();
        }

        return instance;
    }

    @Override
    protected Expression getOperandExpression(StackFrame sf) {
        return getOperandExpression(sf, 1);
    }


    @Override
    final protected Float getOperand(StackFrame sf) {
        return sf.peekFloat(0);
    }

    @Override
    final protected void storeExpression(Expression result, StackFrame sf) {
        sf.setOperandAttr(result);
    }

    @Override
    final protected void storeResult(Expression result, StackFrame sf) {
        sf.popFloat();

        sf.pushFloat(0);
        storeExpression(result, sf);
    }

}
