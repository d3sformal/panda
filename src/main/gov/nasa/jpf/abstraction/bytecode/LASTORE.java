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

import gov.nasa.jpf.jvm.bytecode.ArrayElementInstruction;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

/**
 * Stores a long value into an array
 */
public class LASTORE extends gov.nasa.jpf.jvm.bytecode.LASTORE implements ArrayStoreInstruction {

    private ArrayStoreExecutor executor = new ArrayStoreExecutor();

    @Override
    public Instruction execute(ThreadInfo ti) {
        return executor.execute(this, ti);
    }

    @Override
    public Instruction executeConcrete(ThreadInfo ti) {
        return super.execute(ti);
    }

    @Override
    public AccessExpression getArrayExpression(StackFrame sf) {
        return ExpressionUtil.getAccessExpression(sf.getOperandAttr(3));
    }

    @Override
    public ElementInfo getArray(StackFrame sf) {
        ThreadInfo ti = ThreadInfo.getCurrentThread();

        return ti.getElementInfo(sf.peek(3));
    }

    @Override
    public Expression getSourceExpression(StackFrame sf) {
        return ExpressionUtil.getExpression(sf.getLongOperandAttr());
    }

    @Override
    public Expression getIndexExpression(StackFrame sf) {
        return ExpressionUtil.getExpression(sf.getOperandAttr(2));
    }

    @Override
    public int getIndex(StackFrame sf) {
        return sf.peek(2);
    }

    @Override
    public ArrayElementInstruction getSelf() {
        return this;
    }
}
