package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.impl.NonEmptyAttribute;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;

public class DUP extends gov.nasa.jpf.jvm.bytecode.DUP {
    @Override
    public Instruction execute(ThreadInfo ti) {
        Instruction ret = super.execute(ti);

        StackFrame sf = ti.getModifiableTopFrame();
        Attribute source = Attribute.ensureNotNull((Attribute) sf.getOperandAttr(1));
        Attribute target = Attribute.ensureNotNull((Attribute) sf.getOperandAttr(0));
        Expression value = source.getExpression();

        if (value instanceof AnonymousObject) {
            AnonymousObject object = (AnonymousObject) value;

            sf.setOperandAttr(new NonEmptyAttribute(target.getAbstractValue(), AnonymousObject.create(object.getReference(), true)));
        } else if (value instanceof AnonymousArray) {
            AnonymousArray array = (AnonymousArray) value;

            sf.setOperandAttr(new NonEmptyAttribute(target.getAbstractValue(), AnonymousArray.create(array.getReference(), array.getArrayLength(), true)));
        }

        return ret;
    }
}
