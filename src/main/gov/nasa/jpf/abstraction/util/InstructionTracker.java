package gov.nasa.jpf.abstraction.util;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ReturnInstruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class InstructionTracker extends ListenerAdapter {
	private static boolean running = false;
	
	@Override
	public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {
		String targetClass = vm.getJPF().getConfig().getTarget();

		if (execInsn instanceof InvokeInstruction) {
			MethodInfo method = nextInsn.getMethodInfo();

			if (method.getClassName().equals(targetClass) && method.getName().equals("main")) {
				running = true;
			}
		}
		
		MethodInfo method = execInsn.getMethodInfo();
		if (execInsn instanceof ReturnInstruction && method.getClassName().equals(targetClass) && method.getName().equals("main")) {
			running = false;
		}
		
		if (running) {
			String source = execInsn.getSourceLine() == null ? "" : "'" + execInsn.getSourceLine().trim() + "'";
			System.out.println(execInsn.getClass().getSimpleName() + "\t" + source);
		}
	}
}
