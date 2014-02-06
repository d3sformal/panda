package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ElementInfo;

public class AssertConjunctionHandler extends AssertDisjunctionHandler {
    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
        StackFrame sf = curTh.getTopFrame();

        ElementInfo arrayEI = curTh.getElementInfo(sf.pop());

        checkAssertionSet(arrayEI, curTh, nextInsn);
    }
}
