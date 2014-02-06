package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;

public interface ExecuteInstructionHandler {
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn);
}
