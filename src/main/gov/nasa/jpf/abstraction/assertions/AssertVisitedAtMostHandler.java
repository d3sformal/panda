package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;

public class AssertVisitedAtMostHandler extends AssertHandler {
    @Override
    public void executeInstruction(VM vm, ThreadInfo ti, Instruction insn) {
        StackFrame sf = ti.getTopFrame();

        int limit = sf.pop();

        if (!AssertStateMatchingContext.update(insn, VisitedAtMostAssertion.class, new Integer(limit))) {
            reportError(vm, insn.getLineNumber(), AssertStateMatchingContext.get(insn).getError());
        }
    }
}