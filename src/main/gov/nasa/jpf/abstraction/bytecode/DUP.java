package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class DUP extends gov.nasa.jpf.jvm.bytecode.DUP {
    @Override
    public Instruction execute(ThreadInfo ti) {
        Instruction ret = super.execute(ti);

        StackFrame sf = ti.getModifiableTopFrame();
        Expression source = ExpressionUtil.getExpression(sf.getOperandAttr());

        if (source instanceof AnonymousArray) {
            AnonymousArray array = (AnonymousArray) source;

            sf.setOperandAttr(AnonymousArray.create(array.getReference(), array.getArrayLength(), true));
        } else if (source instanceof AnonymousObject) {
            AnonymousObject object = (AnonymousObject) source;

            sf.setOperandAttr(AnonymousObject.create(object.getReference(), true));
        }

        return ret;
    }
}
