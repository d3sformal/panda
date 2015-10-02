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

import java.util.Stack;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ReturnInstruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.util.RunDetector;

/**
 * Prints the current predicate valuation after each instruction in the target program
 */
public class PredicateValuationMonitor extends ListenerAdapter {

    @Override
    public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {
        if (RunDetector.isRunning() && nextInsn != null) {
            inspect(PredicateAbstraction.getInstance(), nextInsn.getPosition());
        }
    }

    private void inspect(Abstraction abs, int pc) {
        PredicateAbstraction predicate = (PredicateAbstraction) abs;
        String table = predicate.getPredicateValuation().toString(pc);
        System.out.println(
            "--PREDICATES " + predicate.getPredicateValuation().count() + " --\n" +
            table +
            "--------------"
        );
        System.out.flush();
    }
}
