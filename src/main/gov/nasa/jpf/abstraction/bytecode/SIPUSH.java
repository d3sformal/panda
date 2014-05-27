package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.Attribute;

/**
 * Push a short onto the stack
 */
public class SIPUSH extends gov.nasa.jpf.jvm.bytecode.SIPUSH {

    public SIPUSH(int value) {
        super(value);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        Instruction ret = super.execute(ti);

        /**
         * Dont forget to set the symbolic value
         */
        StackFrame sf = ti.getModifiableTopFrame();
        sf.setOperandAttr(new Attribute(null, Constant.create(getValue())));

        return ret;
    }

}
