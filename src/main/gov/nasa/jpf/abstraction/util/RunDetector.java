package gov.nasa.jpf.abstraction.util;

import java.util.Stack;

import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ReturnInstruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class RunDetector {
	private static Stack<Boolean> running = new Stack<Boolean>();
	
	private static boolean detectRunningMethod(String targetClass, MethodInfo method) {
		if (method.getClassName().equals(targetClass)) {
			if (method.getName().equals("main") || method.isClinit()) {
				return true;
			}
		}
		
		return false;
	}
	
	public static void initialiseNotRunning() {
		running.push(false);
	}
	
	public static void detectRunning(VM vm, Instruction nextInsn, Instruction execInsn) {
		String targetClass = vm.getJPF().getConfig().getTarget();

		if (execInsn instanceof InvokeInstruction) {
			if (detectRunningMethod(targetClass, nextInsn.getMethodInfo())) {
				running.pop();
				running.push(true);
			}
		}
		
		if (execInsn instanceof ReturnInstruction) {
			if (detectRunningMethod(targetClass, execInsn.getMethodInfo())) {
				running.pop();
				running.push(false);
			}
		}
	}
	
	public static void detectRunning(Search search) {
		VM vm = search.getVM();
		ThreadInfo ti = vm.getCurrentThread();
		Instruction nextInsn = ti.getNextPC();
		Instruction execInsn = ti.getPC();
		
		detectRunning(search.getVM(), nextInsn, execInsn);
	}

	public static void advance() {
		running.push(running.lastElement());
	}

	public static void backtrack() {
		running.pop();
	}

	public static boolean isRunning() {
		return running.lastElement();
	}
}
