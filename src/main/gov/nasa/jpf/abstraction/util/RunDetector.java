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

import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ReturnInstruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

/**
 * Is responsible for determining whether the currently executed code is part of the targeted execution or not
 */
public class RunDetector {
    private static Stack<RunningState> runningHistory;
    private static RunningState running;

    private static boolean detectRunningMethod(String targetClass, String targetMethod, MethodInfo method) {
        if (method.getClassName().equals(targetClass)) {
            if (method.getFullName().equals(targetClass + "." + targetMethod) || method.isClinit()) {
                return true;
            }
        }

        return false;
    }

    public static void initialiseNotRunning() {
        runningHistory = new Stack<RunningState>();
        running = new RunningState();

        runningHistory.add(running.clone());
    }

    public static void detectRunning(VM vm, Instruction nextInsn, Instruction execInsn) {
        String targetClass = vm.getJPF().getConfig().getTarget();
        String targetMethod = vm.getJPF().getConfig().getTargetEntry();

        if (targetMethod == null) {
            targetMethod = "main([Ljava/lang/String;)V";
        }

        if (execInsn instanceof InvokeInstruction) {
            if (detectRunningMethod(targetClass, targetMethod, nextInsn.getMethodInfo())) {
                running.enter();
                return;
            }
        }

        if (execInsn instanceof ReturnInstruction) {
            if (detectRunningMethod(targetClass, targetMethod, execInsn.getMethodInfo())) {
                running.leave();
                return;
            }
        }

        running.touch();
    }

    public static void advance() {
        runningHistory.push(running.clone());
    }

    public static void backtrack() {
        runningHistory.pop();
        running = runningHistory.peek().clone();
    }

    public static boolean isRunning() {
        return running.hasBeenRunning();
    }

    public static boolean isInLibrary(ThreadInfo ti) {
        for (StackFrame sf : ti) {
            if (sf.getMethodInfo().getFullName().startsWith("java.")) {
                return true;
            }
        }

        return false;
    }
}
