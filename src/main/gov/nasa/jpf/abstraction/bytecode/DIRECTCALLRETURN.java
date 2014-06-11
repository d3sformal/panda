package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class DIRECTCALLRETURN extends gov.nasa.jpf.jvm.bytecode.DIRECTCALLRETURN {

    @Override
    public Instruction execute(ThreadInfo ti) {

        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);
        StackFrame before = ti.getTopFrame();

        Instruction actualNextInsn = super.execute(ti);

        StackFrame after = ti.getTopFrame();

        if (JPFInstructionAdaptor.testDirectCallReturnInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        // We do not add special scopes for synthetic stack frames yet, therefore we cannot return from them (would remove a different frame, whose return would then remove yet another frame)
        //PredicateAbstraction.getInstance().processMethodReturn(ti, before, after);

        return actualNextInsn;
    }
}
