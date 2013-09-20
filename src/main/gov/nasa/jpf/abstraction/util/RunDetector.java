package gov.nasa.jpf.abstraction.util;

import java.util.Stack;

import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ReturnInstruction;
import gov.nasa.jpf.vm.VM;

/**
 * Is responsible for determining whether the currently executed code is part of the targeted execution or not
 */
public class RunDetector {
	private static Stack<RunningState> runningHistory = new Stack<RunningState>();
	private static RunningState running = new RunningState();
	
	private static boolean detectRunningMethod(String targetClass, MethodInfo method) {
		if (method.getClassName().equals(targetClass)) {
			if (method.getName().equals("main") || method.isClinit()) {
				return true;
			}
		}
		
		return false;
	}
	
	public static void initialiseNotRunning() {
		runningHistory.add(running.clone());
	}
	
	public static void detectRunning(VM vm, Instruction nextInsn, Instruction execInsn) {
		String targetClass = vm.getJPF().getConfig().getTarget();

		if (execInsn instanceof InvokeInstruction) {
			if (detectRunningMethod(targetClass, nextInsn.getMethodInfo())) {
				running.enter();
				return;
			}
		}
		
		if (execInsn instanceof ReturnInstruction) {
			if (detectRunningMethod(targetClass, execInsn.getMethodInfo())) {
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
}
