package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ElementInfo;

public class AssertConjunctionHandler extends AssertPredicateHandler {

    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
        StackFrame sf = curTh.getModifiableTopFrame();

        ElementInfo arrayEI = curTh.getElementInfo(sf.pop());

        try {
            checkAssertionSet(arrayEI, curTh);
        } catch (Exception e) {
            reportError(vm, nextInsn.getLineNumber(), e.getMessage());
        }
    }

}