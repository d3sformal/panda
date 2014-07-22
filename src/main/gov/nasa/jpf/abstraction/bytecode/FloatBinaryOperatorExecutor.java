package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;

import gov.nasa.jpf.abstraction.common.Expression;

/**
 * Implements type specific parts of the operation ( @see gov.nasa.jpf.abstraction.bytecode.BinaryOperatorExecutor )
 *   - layout of operands (and their attributes) on the stack (type size comes into play)
 */
public class FloatBinaryOperatorExecutor extends BinaryOperatorExecutor<Float, Float> {

    private static FloatBinaryOperatorExecutor instance;

    public static FloatBinaryOperatorExecutor getInstance() {
        if (instance == null) {
            instance = new FloatBinaryOperatorExecutor();
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
    final protected Float getFirstOperand(StackFrame sf) {
        return sf.peekFloat(1);
    }

    @Override
    final protected Float getSecondOperand(StackFrame sf) {
        return sf.peekFloat(0);
    }

    @Override
    final protected void storeExpression(Expression result, StackFrame sf) {
        sf.setOperandAttr(result);
    }

}
