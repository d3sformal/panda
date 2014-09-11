package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.state.universe.Reference;

public class NEW extends gov.nasa.jpf.jvm.bytecode.NEW {

    public NEW(String clsDescriptor) {
        super(clsDescriptor);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);

        Instruction actualNextInsn = super.execute(ti);

        if (JPFInstructionAdaptor.testNewInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        StackFrame sf = ti.getModifiableTopFrame();
        ElementInfo ei = ti.getElementInfo(sf.peek());
        AnonymousObject object = AnonymousObject.create(new Reference(ei));

        if (ei.getClassInfo().getName().equals("java.lang.AssertionError")) {
            AssertionErrorTracker.setAssertionErrorAllocationSite(ei, sf.getMethodInfo(), getPosition());
        }

        PredicateAbstraction.getInstance().processNewObject(object, actualNextInsn.getMethodInfo(), actualNextInsn.getPosition());
        sf.setOperandAttr(object);

        return actualNextInsn;
    }

}
