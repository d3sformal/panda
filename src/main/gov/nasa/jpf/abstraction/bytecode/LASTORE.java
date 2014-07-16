package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.jvm.bytecode.ArrayElementInstruction;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Stores a long value into an array
 */
public class LASTORE extends gov.nasa.jpf.jvm.bytecode.LASTORE implements ArrayStoreInstruction {

    private ArrayStoreExecutor executor = new ArrayStoreExecutor();

    @Override
    public Instruction execute(ThreadInfo ti) {
        return executor.execute(this, ti);
    }

    @Override
    public Instruction executeConcrete(ThreadInfo ti) {
        return super.execute(ti);
    }

    @Override
    public AccessExpression getArrayExpression(StackFrame sf) {
        return ExpressionUtil.getAccessExpression(sf.getOperandAttr(3));
    }

    @Override
    public ElementInfo getArray(StackFrame sf) {
        ThreadInfo ti = ThreadInfo.getCurrentThread();

        return ti.getElementInfo(sf.peek(3));
    }

    @Override
    public Expression getSourceExpression(StackFrame sf) {
        return ExpressionUtil.getExpression(sf.getLongOperandAttr());
    }

    @Override
    public Expression getIndexExpression(StackFrame sf) {
        return ExpressionUtil.getExpression(sf.getOperandAttr(2));
    }

    @Override
    public ArrayElementInstruction getSelf() {
        return this;
    }
}
