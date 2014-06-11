package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.KernelState;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Convert float to int
 * ..., value => ..., result
 */
public class F2I extends gov.nasa.jpf.jvm.bytecode.F2I implements TypeConvertor {

    private TypeConversionExecutor exec = new TypeConversionExecutor(new FloatManipulator(), new IntegerManipulator());

    @Override
    public Instruction execute(ThreadInfo ti) {
        return exec.execute(ti, this);
    }

    @Override
    public Instruction executeConcrete(ThreadInfo ti) {
        return super.execute(ti);
    }

}
