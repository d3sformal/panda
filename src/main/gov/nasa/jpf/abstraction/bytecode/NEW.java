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

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.state.universe.Reference;

public class NEW extends gov.nasa.jpf.jvm.bytecode.NEW {

    public NEW(String clsDescriptor) {
        super(clsDescriptor);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);

        Instruction actualNextInsn = super.execute(ti);

        if (JPFInstructionAdaptor.testNewInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        StackFrame sf = ti.getModifiableTopFrame();
        ElementInfo ei = ti.getElementInfo(sf.peek());
        AnonymousObject object = AnonymousObject.create(new Reference(ei));

        if (ei.getClassInfo().getName().equals("java.lang.AssertionError")) {
            AssertionErrorTracker.setAssertionErrorAllocationSite(ei, sf.getMethodInfo(), getPosition());
        }

        PredicateAbstraction.getInstance().processNewObject(object, actualNextInsn.getMethodInfo(), actualNextInsn.getPosition());
        sf.setOperandAttr(object);

        return actualNextInsn;
    }

}
