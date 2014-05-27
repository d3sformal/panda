package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.GlobalAbstraction;

public class ANEWARRAY extends gov.nasa.jpf.jvm.bytecode.ANEWARRAY {

    public ANEWARRAY(String typeDescriptor) {
        super(typeDescriptor);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        StackFrame sf = ti.getTopFrame();
        Expression length = Attribute.getExpression(sf.getOperandAttr());

        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);

        Instruction actualNextInsn = super.execute(ti);

        if (JPFInstructionAdaptor.testNewArrayInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        ElementInfo array = ti.getElementInfo(sf.peek());
        AnonymousArray expression = AnonymousArray.create(new Reference(array), length);

        GlobalAbstraction.getInstance().processNewClass(ti, array.getClassInfo());

        GlobalAbstraction.getInstance().processNewObject(expression);

        sf = ti.getModifiableTopFrame();
        sf.setOperandAttr(new Attribute(null, expression));

        return actualNextInsn;
    }

}
