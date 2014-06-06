package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.impl.NullExpression;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class ACONST_NULL extends gov.nasa.jpf.jvm.bytecode.ACONST_NULL {
    @Override
    public Instruction execute(ThreadInfo ti) {
        Instruction ret = super.execute(ti);
        StackFrame sf = ti.getModifiableTopFrame();

        sf.setOperandAttr(NullExpression.create());

        return ret;
    }
}
