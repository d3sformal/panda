package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class LLOAD extends gov.nasa.jpf.jvm.bytecode.LLOAD {

    public LLOAD(int index) {
        super(index);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        Instruction actualNextInsn = super.execute(ti);

        DefaultRoot path = DefaultRoot.create(getLocalVariableName(), getLocalVariableIndex());

        StackFrame sf = ti.getModifiableTopFrame();
        sf.setLongOperandAttr(path);

        PredicateAbstraction.getInstance().informAboutPrimitiveLocalVariable(path);

        return actualNextInsn;
    }
}
