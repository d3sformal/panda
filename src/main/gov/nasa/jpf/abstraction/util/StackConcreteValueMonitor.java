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
package gov.nasa.jpf.abstraction.util;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class StackConcreteValueMonitor extends ListenerAdapter {

    @Override
    public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {
        if (RunDetector.isRunning()) {
            StackFrame sf = curTh.getTopFrame();
            if (sf != null) {
                inspect(curTh, sf);
            }
        }
    }

    public static void inspect(ThreadInfo ti, StackFrame sf) {
        //NOT HANDLING LONGS...
        System.out.println("--CONCRETE VALUES --");
        for (int i = 0; i <= (sf.getTopPos() - sf.getLocalVariableCount()); ++i) {
            System.out.print(i + ": ");
            if (sf.isReferenceSlot(i)) {
                ElementInfo ei = ti.getElementInfo(sf.peek(i));

                if (ei == null) {
                    System.out.print(sf.peek(i) + " (null)");
                } else {
                    System.out.print(sf.peek(i));

                    if (ei.isStringObject()) {
                        System.out.print(" -> \"" + ei.asString() + "\"");
                    }
                }
            } else {
                System.out.print(sf.peek(i));
            }
            System.out.println();
        }
        System.out.println("--LOCAL VARS --");
        for (int i = 0; i < sf.getLocalVariableCount(); ++i) {
            LocalVarInfo var = sf.getLocalVarInfo(i);

            System.out.print((var == null ? null : var.getName()) + ": " + sf.getLocalVariable(i) + " " + (var == null ? "null" : sf.isLocalVariableRef(var.getSlotIndex())));
            if (var != null) {
                if (sf.isLocalVariableRef(var.getSlotIndex())) {
                    ElementInfo ei = ti.getElementInfo(sf.getLocalVariable(var.getSlotIndex()));

                    if (ei != null && ei.isStringObject()) {
                        System.out.print(" -> \"" + ei.asString() + "\"");
                    }
                }
            }
            System.out.println();
        }
        System.out.println("--------------");
    }
}
