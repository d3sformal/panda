package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.PredicateAbstraction;

/**
 * Bytecode instruction DRETURN
 * ... => double
 */
public class DRETURN extends gov.nasa.jpf.jvm.bytecode.DRETURN {

    @Override
    public Instruction execute(ThreadInfo ti) {

        /**
         * Find out what is expected to follow
         */
        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);
        StackFrame before = ti.getTopFrame();

        Instruction actualNextInsn = super.execute(ti);

        StackFrame after = ti.getTopFrame();

        /**
         * Test whether the instruction was successfully executed (a choice may have been generated)
         */
        if (JPFInstructionAdaptor.testReturnInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        /**
         * Inform abstractions about this event
         */
        PredicateAbstraction.getInstance().processMethodReturn(ti, before, after);

        return actualNextInsn;
    }
}
