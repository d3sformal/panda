package gov.nasa.jpf.abstraction.util;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;

public class InstructionTracker extends ListenerAdapter {	
	@Override
	public void executeInstruction(VM vm, ThreadInfo curTh, Instruction execInsn) {		
		if (RunDetector.isRunning()) {
			String source = execInsn.getSourceLine() == null ? "" : "'" + execInsn.getSourceLine().trim() + "'";
			System.out.print("Current instruction [first step = " + curTh.isFirstStepInsn() +  "]: " + execInsn.getClass().getSimpleName());
            if (execInsn instanceof InvokeInstruction) {
                InvokeInstruction invkInsn = (InvokeInstruction) execInsn;

                System.out.print(" (" + invkInsn.getInvokedMethodClassName() + "." + invkInsn.getInvokedMethodName() + ")");
            }
            System.out.println("\t" + source);
            System.out.println("\t in method: " + execInsn.getMethodInfo().getFullName());
		}
	}
}
