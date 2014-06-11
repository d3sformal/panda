package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.KernelState;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Convert double to float
 * ..., value => ..., result
 */
public class D2F extends gov.nasa.jpf.jvm.bytecode.D2F implements TypeConvertor {

    private TypeConversionExecutor exec = new TypeConversionExecutor(new DoubleManipulator(), new FloatManipulator());

    @Override
    public Instruction execute(ThreadInfo ti) {
        return exec.execute(ti, this);
    }

    @Override
    public Instruction executeConcrete(ThreadInfo ti) {
        return super.execute(ti);
    }

}
