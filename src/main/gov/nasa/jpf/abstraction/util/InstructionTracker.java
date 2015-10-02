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
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class InstructionTracker extends ListenerAdapter {
    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction execInsn) {
        if (RunDetector.isRunning()) {
            String source = execInsn.getSourceLine() == null ? "" : "'" + execInsn.getSourceLine().trim() + "'";
            System.out.print("Current instruction [first step = " + curTh.isFirstStepInsn() +  "]: " + execInsn.getPosition() + ": " + execInsn.getClass().getSimpleName());
            if (execInsn instanceof InvokeInstruction) {
                InvokeInstruction invkInsn = (InvokeInstruction) execInsn;

                System.out.print(" (" + invkInsn.getInvokedMethodClassName() + "." + invkInsn.getInvokedMethodName() + ")");
            }
            System.out.println("\t" + source);
            System.out.println("\t in method: " + execInsn.getMethodInfo().getFullName());
        }
    }
}
