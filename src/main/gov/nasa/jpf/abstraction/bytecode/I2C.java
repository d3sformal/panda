package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.KernelState;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.common.Expression;

/**
 * Convert int to char
 * ..., value => ..., result
 */
public class I2C extends gov.nasa.jpf.jvm.bytecode.I2C implements TypeConvertor {

    private TypeConversionExecutor exec = new TypeConversionExecutor(new IntegerManipulator(), new CharacterManipulator());

    @Override
    public Instruction execute(ThreadInfo ti) {
        return exec.execute(ti, this);
    }

    @Override
    public Instruction executeConcrete(ThreadInfo ti) {
        return super.execute(ti);
    }

}