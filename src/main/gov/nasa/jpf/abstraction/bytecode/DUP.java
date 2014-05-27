package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;

public class DUP extends gov.nasa.jpf.jvm.bytecode.DUP {
    @Override
    public Instruction execute(ThreadInfo ti) {
        Instruction ret = super.execute(ti);

        StackFrame sf = ti.getModifiableTopFrame();
        Attribute source = Attribute.getAttribute(sf.getOperandAttr());
        Expression value = Attribute.getExpression(source);

        if (value instanceof AnonymousArray) {
            AnonymousArray array = (AnonymousArray) value;

            sf.setOperandAttr(new Attribute(Attribute.getAbstractValue(source), AnonymousArray.create(array.getReference(), array.getArrayLength(), true)));
        } else if (value instanceof AnonymousObject) {
            AnonymousObject object = (AnonymousObject) value;

            sf.setOperandAttr(new Attribute(Attribute.getAbstractValue(source), AnonymousObject.create(object.getReference(), true)));
        }

        return ret;
    }
}
