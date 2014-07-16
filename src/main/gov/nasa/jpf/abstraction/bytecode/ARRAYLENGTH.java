package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayLengthRead;

/**
 * Retrieves the lenght of an array
 */
public class ARRAYLENGTH extends gov.nasa.jpf.jvm.bytecode.ARRAYLENGTH {
    @Override
    public Instruction execute(ThreadInfo ti) {
        StackFrame sf = ti.getTopFrame();

        AccessExpression path = ExpressionUtil.getAccessExpression(sf.getOperandAttr());
        AccessExpression length = DefaultArrayLengthRead.create(path);

        Instruction ret = super.execute(ti);

        /**
         * Store the symbolic value for predicate abstraction purposes
         */
        sf = ti.getModifiableTopFrame();
        sf.addOperandAttr(length);

        return ret;
    }
}
