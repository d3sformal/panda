package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.util.ExpressionUtil;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Stores a value from a stack to a local variable and informs abstractions about such an event
 */
public class DSTORE extends gov.nasa.jpf.jvm.bytecode.DSTORE {

    public DSTORE(int index) {
        super(index);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        StackFrame sf = ti.getTopFrame();
        Expression from = ExpressionUtil.getExpression(sf.getLongOperandAttr());

        Instruction actualNextInsn = super.execute(ti);

        DefaultRoot to = DefaultRoot.create(getLocalVariableName(), getLocalVariableIndex());

        sf = ti.getModifiableTopFrame();

        /**
         * Remember what has been stored here
         */
        sf.setLocalAttr(getLocalVariableIndex(), from);

        /**
         * Inform the abstractions that a primitive value of a local variable may have changed
         */
        PredicateAbstraction.getInstance().informAboutPrimitiveLocalVariable(to);
        PredicateAbstraction.getInstance().processPrimitiveStore(from, to);

        return actualNextInsn;
    }
}
