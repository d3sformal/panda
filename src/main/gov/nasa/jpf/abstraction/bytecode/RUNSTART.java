package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.StackFrame;

import gov.nasa.jpf.abstraction.impl.NonEmptyAttribute;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;

public class RUNSTART extends gov.nasa.jpf.jvm.bytecode.RUNSTART {
    @Override
    public Instruction execute(ThreadInfo ti) {
        Instruction ret = super.execute(ti);

        StackFrame sf = ti.getModifiableTopFrame();

        sf.setOperandAttr(new NonEmptyAttribute(null, AnonymousObject.create(new Reference(ti.getElementInfo(sf.peek())))));

        return ret;
    }
}
