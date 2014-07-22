package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;

import gov.nasa.jpf.abstraction.common.Expression;

/**
 * Implements type specific parts of the operation ( @see gov.nasa.jpf.abstraction.bytecode.UnaryOperatorExecutor )
 *   - layout of the stack (type size comes into play)
 */
public class DoubleUnaryOperatorExecutor extends UnaryOperatorExecutor<Double> {

    private static DoubleUnaryOperatorExecutor instance;

    public static DoubleUnaryOperatorExecutor getInstance() {
        if (instance == null) {
            instance = new DoubleUnaryOperatorExecutor();
        }

        return instance;
    }

    @Override
    protected Expression getOperandExpression(StackFrame sf) {
        return getOperandExpression(sf, 1);
    }


    @Override
    final protected Double getOperand(StackFrame sf) {
        return sf.peekDouble(0);
    }

    @Override
    final protected void storeExpression(Expression result, StackFrame sf) {
        sf.setLongOperandAttr(result);
    }

    @Override
    final protected void storeResult(Expression result, StackFrame sf) {
        sf.popDouble();

        sf.pushDouble(0);
        storeExpression(result, sf);
    }

}
