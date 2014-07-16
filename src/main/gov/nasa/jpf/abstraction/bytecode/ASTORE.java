package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class ASTORE extends gov.nasa.jpf.jvm.bytecode.ASTORE {

    public ASTORE(int index) {
        super(index);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        StackFrame sf = ti.getTopFrame();
        Expression from = ExpressionUtil.getExpression(sf.getOperandAttr());

        Instruction actualNextInsn = super.execute(ti);

        DefaultRoot to = DefaultRoot.create(getLocalVariableName(), getLocalVariableIndex());

        sf = ti.getModifiableTopFrame();
        sf.setLocalAttr(getLocalVariableIndex(), from);

        PredicateAbstraction.getInstance().informAboutStructuredLocalVariable(to);
        PredicateAbstraction.getInstance().processObjectStore(from, to);

        AnonymousExpressionTracker.notifyPopped(from);

        return actualNextInsn;
    }
}
