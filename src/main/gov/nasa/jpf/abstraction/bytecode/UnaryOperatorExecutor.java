package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.util.ExpressionUtil;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Implementation of unary operations regardless their concrete type.
 */
public abstract class UnaryOperatorExecutor<T> {

    final public Instruction execute(AbstractUnaryOperator<T> op, ThreadInfo ti) {
        StackFrame sf = ti.getModifiableTopFrame();

        Expression expr = getExpression(sf);
        Expression result = op.getResult(expr);

        Instruction ret = op.executeConcrete(ti);

        storeExpression(result, sf);

        return ret;
    }

    protected Expression getExpression(StackFrame sf, int index) {
        return ExpressionUtil.getExpression(sf.getOperandAttr(index));
    }

    abstract protected Expression getExpression(StackFrame sf);
    abstract protected T getOperand(StackFrame sf);
    abstract protected void storeExpression(Expression result, StackFrame sf);
    abstract protected void storeResult(Expression result, StackFrame sf);
}
