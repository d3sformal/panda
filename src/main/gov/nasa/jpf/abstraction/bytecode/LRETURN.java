package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class LRETURN extends gov.nasa.jpf.jvm.bytecode.LRETURN {

    @Override
    public Instruction execute(ThreadInfo ti) {

        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);
        StackFrame before = ti.getTopFrame();

        Instruction actualNextInsn = super.execute(ti);

        StackFrame after = ti.getTopFrame();

        if (JPFInstructionAdaptor.testReturnInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        PredicateAbstraction.getInstance().processMethodReturn(ti, before, after);

        return actualNextInsn;
    }
}
