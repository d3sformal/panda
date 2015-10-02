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
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ClassLoaderList;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class StaticClassObjectMonitor extends ListenerAdapter {
    @Override
    public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {
        ClassLoaderList clis = vm.getClassLoaderList();

        String head = ">>> " + execInsn.getMnemonic() + " <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<";
        String foot = head.replaceAll(".", "=");

        System.out.println(head);
        for (ClassLoaderInfo cli : clis) {
            for (ElementInfo ei : cli.getStatics()) {
                if (ei.getClassInfo().isInitialized()) {
                    StaticClassObjectTracker.dumpElementInfo(curTh, ei);
                }
            }
        }

        System.out.println(foot);
    }
}
