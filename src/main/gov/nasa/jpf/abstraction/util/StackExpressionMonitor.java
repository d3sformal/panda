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
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ReturnInstruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.abstraction.util.RunDetector;

/**
 * Prints the current state of the symbolic stack after each instruction in the target program
 */
public class StackExpressionMonitor extends ListenerAdapter {

    @Override
    public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {
        if (RunDetector.isRunning()) {
            StackFrame sf = curTh.getTopFrame();

            if (sf != null) {
                inspect(sf);
            }
        }
    }

    public static void inspect(StackFrame sf) {
        System.out.println("--EXPRESSIONS --");

        for (int i = 0; i <= (sf.getTopPos() - sf.getLocalVariableCount()); i++) {
            Expression expr = ExpressionUtil.getExpression(sf.getOperandAttr(i));

            if (expr != null) {
                System.out.println("[" + i + "]: " + expr);
            } else {
                System.out.println("[" + i + "]: null");
            }
        }

        System.out.println("--------------");
    }
}
