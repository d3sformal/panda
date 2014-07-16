package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;

import gov.nasa.jpf.abstraction.common.Expression;

/**
 * Implements type specific parts of the operation ( @see gov.nasa.jpf.abstraction.bytecode.BinaryOperatorExecutor )
 *   - layout of operands (and their attributes) on the stack (type size comes into play)
 */
public class LongBinaryOperatorExecutor extends BinaryOperatorExecutor<Long> {

    private static LongBinaryOperatorExecutor instance;

    public static LongBinaryOperatorExecutor getInstance() {
        if (instance == null) {
            instance = new LongBinaryOperatorExecutor();
        }

        return instance;
    }

    @Override
    protected Expression getLeftHandSideExpression(StackFrame sf) {
        return getExpression(sf, 3);
    }

    @Override
    protected Expression getRightHandSideExpression(StackFrame sf) {
        return getExpression(sf, 1);
    }

    @Override
    protected Long getLeftHandSideOperand(StackFrame sf) {
        return sf.peekLong(2);
    }

    @Override
    protected Long getRightHandSideOperand(StackFrame sf) {
        return sf.peekLong(0);
    }

    @Override
    final protected void storeExpression(Expression result, StackFrame sf) {
        sf.setLongOperandAttr(result);
    }

}
