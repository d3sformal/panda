package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.Attribute;

/**
 * Loads a constant from a const pool onto the stack
 * ... => ..., long
 */
public class LCONST extends gov.nasa.jpf.jvm.bytecode.LCONST {

    public LCONST(int value) {
        super(value);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        Instruction ret = super.execute(ti);

        StackFrame sf = ti.getModifiableTopFrame();

        /**
         * Symbolic execution
         */
        sf.setLongOperandAttr(new Attribute(null, Constant.create(getValue())));

        return ret;
    }

}
