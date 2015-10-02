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

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;

public class INVOKEINTERFACE extends gov.nasa.jpf.jvm.bytecode.INVOKEINTERFACE {

    public INVOKEINTERFACE(String clsName, String methodName, String methodSignature) {
        super(clsName, methodName, methodSignature);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {

        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);
        StackFrame before = ti.getTopFrame();

        Instruction actualNextInsn = super.execute(ti);

        StackFrame after = ti.getTopFrame();

        if (JPFInstructionAdaptor.testVirtualInvocationInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        /**
         * Collect current symbolic arguments and store them as attributes of the method
         * this allows predicate abstraction to reason about argument assignment
         *
         * These copies of attributes are preserved during the execution of the method and may be used after return.
         * We cannot rely solely on argument attributes for this reason.
         */
        Expression[] arguments = new Expression[after.getMethodInfo().getNumberOfStackArguments()];

        for (int i = 0; i < after.getMethodInfo().getNumberOfStackArguments(); ++i) {
            arguments[after.getMethodInfo().getNumberOfStackArguments() - i - 1] = ExpressionUtil.getExpression(before.getOperandAttr(i));
        }

        after.setFrameAttr(arguments);

        PredicateAbstraction.getInstance().processMethodCall(ti, before, after);

        for (int i = 0; i < after.getMethodInfo().getNumberOfStackArguments(); ++i) {
            AnonymousExpressionTracker.notifyPopped(arguments[i], 1);
        }

        return actualNextInsn;
    }
}
