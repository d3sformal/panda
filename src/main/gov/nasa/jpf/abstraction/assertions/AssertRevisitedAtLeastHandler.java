package gov.nasa.jpf.abstraction.assertions;

import java.util.Set;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class AssertRevisitedAtLeastHandler extends AssertHandler {
    @Override
    public void executeInstruction(VM vm, ThreadInfo ti, Instruction insn) {
        StackFrame sf = ti.getTopFrame();

        int limit = sf.pop();

        AssertStateMatchingContext.getAssertion(insn, RevisitedAtLeastAssertion.class).update(new Integer(limit));
    }

    @Override
    public void finish() {
        for (Instruction insn : AssertStateMatchingContext.getLocations()) {
            LocationAssertion locationAssertion = AssertStateMatchingContext.get(insn);

            if (locationAssertion instanceof RevisitedAtLeastAssertion && locationAssertion.isViolated()) {
                reportError(VM.getVM(), insn.getLineNumber(), AssertStateMatchingContext.get(insn).getError());
            }
        }
    }
}
