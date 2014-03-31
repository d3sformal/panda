package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;

public abstract class ExecuteInstructionHandler {
    public abstract void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn);
    public void searchFinished() {
        finish();
    }
    public void finish() {
    }
}
