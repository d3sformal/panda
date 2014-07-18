package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;

import gov.nasa.jpf.abstraction.common.Expression;

/**
 * Implements type specific parts of the operation ( @see gov.nasa.jpf.abstraction.bytecode.BinaryOperatorExecutor )
 *   - layout of operands (and their attributes) on the stack (type size comes into play)
 */
public class DoubleBinaryOperatorExecutor extends BinaryOperatorExecutor<Double, Double> {

    private static DoubleBinaryOperatorExecutor instance;

    public static DoubleBinaryOperatorExecutor getInstance() {
        if (instance == null) {
            instance = new DoubleBinaryOperatorExecutor();
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
    final protected Double getLeftHandSideOperand(StackFrame sf) {
        return sf.peekDouble(2);
    }

    @Override
    final protected Double getRightHandSideOperand(StackFrame sf) {
        return sf.peekDouble(0);
    }

    @Override
    final protected void storeExpression(Expression result, StackFrame sf) {
        sf.setLongOperandAttr(result);
    }

}
