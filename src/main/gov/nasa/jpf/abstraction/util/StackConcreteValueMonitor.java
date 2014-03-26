package gov.nasa.jpf.abstraction.util;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.VM;

public class StackConcreteValueMonitor extends ListenerAdapter {
	
	@Override
	public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {
		if (RunDetector.isRunning()) {
			StackFrame sf = curTh.getTopFrame();
            if (sf != null) {
    			inspect(curTh, sf);
            }
		}
	}

	public static void inspect(ThreadInfo ti, StackFrame sf) {
		//NOT HANDLING LONGS...
		System.out.println("--CONCRETE VALUES --");
		for (int i = 0; i <= (sf.getTopPos() - sf.getLocalVariableCount()); ++i) {
			System.out.print(i + ": " + sf.peek(i));
            if (sf.isReferenceSlot(i)) {
                ElementInfo ei = ti.getElementInfo(i);

                if (ei != null && ei.isStringObject()) {
                    System.out.print(" -> \"" + ei.asString() + "\"");
                }
            }
            System.out.println();
		}
		System.out.println("--LOCAL VARS --");
		for (int i = 0; i < sf.getLocalVariableCount(); ++i) {
			LocalVarInfo var = sf.getLocalVarInfo(i);
			
			System.out.println((var == null ? null : var.getName()) + ": " + sf.getLocalVariable(i));
		}
		System.out.println("--------------");
	}
}


