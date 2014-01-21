package gov.nasa.jpf.abstraction.util;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class InstructionTracker extends ListenerAdapter {	
	@Override
	public void executeInstruction(VM vm, ThreadInfo curTh, Instruction execInsn) {		
		if (RunDetector.isRunning()) {
			String source = execInsn.getSourceLine() == null ? "" : "'" + execInsn.getSourceLine().trim() + "'";
			System.out.println("Current instruction [first step = " + curTh.isFirstStepInsn() +  "]: " + execInsn.getClass().getSimpleName() + "\t" + source);
		}
	}
}
