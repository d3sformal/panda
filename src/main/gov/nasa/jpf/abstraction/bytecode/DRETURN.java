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

/**
 * Bytecode instruction DRETURN
 * ... => double
 */
public class DRETURN extends gov.nasa.jpf.jvm.bytecode.DRETURN {

    @Override
    public Instruction execute(ThreadInfo ti) {

        /**
         * Find out what is expected to follow
         */
        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);
        StackFrame before = ti.getTopFrame();

        Instruction actualNextInsn = super.execute(ti);

        StackFrame after = ti.getTopFrame();

        /**
         * Test whether the instruction was successfully executed (a choice may have been generated)
         */
        if (JPFInstructionAdaptor.testReturnInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        /**
         * Inform abstractions about this event
         */
        PredicateAbstraction.getInstance().processMethodReturn(ti, before, after);

        return actualNextInsn;
    }
}
