package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;

import gov.nasa.jpf.abstraction.common.Expression;

/**
 * Implements type specific parts of the operation ( @see gov.nasa.jpf.abstraction.bytecode.BinaryOperatorExecutor )
 *   - layout of operands (and their attributes) on the stack (type size comes into play)
 */
public class LongIntegerBinaryOperatorExecutor extends BinaryOperatorExecutor<Long, Integer> {

    private static LongIntegerBinaryOperatorExecutor instance;

    public static LongIntegerBinaryOperatorExecutor getInstance() {
        if (instance == null) {
            instance = new LongIntegerBinaryOperatorExecutor();
        }

        return instance;
    }

    @Override
    protected Expression getLeftHandSideExpression(StackFrame sf) {
        return getExpression(sf, 2);
    }

    @Override
    protected Expression getRightHandSideExpression(StackFrame sf) {
        return getExpression(sf, 0);
    }

    @Override
    protected Long getLeftHandSideOperand(StackFrame sf) {
        return sf.peekLong(1);
    }

    @Override
    protected Integer getRightHandSideOperand(StackFrame sf) {
        return sf.peek(0);
    }

    @Override
    final protected void storeExpression(Expression result, StackFrame sf) {
        sf.setLongOperandAttr(result);
    }

}
