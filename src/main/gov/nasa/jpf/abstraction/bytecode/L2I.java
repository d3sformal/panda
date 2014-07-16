package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.KernelState;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.common.Expression;

/**
 * Convert long to int
 * ..., value => ..., result
 */
public class L2I extends gov.nasa.jpf.jvm.bytecode.L2I implements TypeConvertor {

    private TypeConversionExecutor exec = new TypeConversionExecutor(new LongManipulator(), new IntegerManipulator());

    @Override
    public Instruction execute(ThreadInfo ti) {
        return exec.execute(ti, this);
    }

    @Override
    public Instruction executeConcrete(ThreadInfo ti) {
        return super.execute(ti);
    }

}
