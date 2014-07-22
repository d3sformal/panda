package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;

import gov.nasa.jpf.abstraction.common.Expression;

/**
 * Implements type specific parts of the operation ( @see gov.nasa.jpf.abstraction.bytecode.BinaryOperatorExecutor )
 *   - layout of operands (and their attributes) on the stack (type size comes into play)
 */
public class IntegerBinaryOperatorExecutor extends BinaryOperatorExecutor<Integer, Integer> {

    private static IntegerBinaryOperatorExecutor instance;

    public static IntegerBinaryOperatorExecutor getInstance() {
        if (instance == null) {
            instance = new IntegerBinaryOperatorExecutor();
        }

        return instance;
    }

    @Override
    protected Expression getFirstOperandExpression(StackFrame sf) {
        return getOperandExpression(sf, 1);
    }

    @Override
    protected Expression getSecondOperandExpression(StackFrame sf) {
        return getOperandExpression(sf, 0);
    }

    @Override
    protected Integer getFirstOperand(StackFrame sf) {
        return sf.peek(1);
    }

    @Override
    protected Integer getSecondOperand(StackFrame sf) {
        return sf.peek(0);
    }

    @Override
    final protected void storeExpression(Expression result, StackFrame sf) {
        sf.setOperandAttr(result);
    }

}
