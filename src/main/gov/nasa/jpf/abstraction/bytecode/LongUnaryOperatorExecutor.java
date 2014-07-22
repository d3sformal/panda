package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;

import gov.nasa.jpf.abstraction.common.Expression;

/**
 * Implements type specific parts of the operation ( @see gov.nasa.jpf.abstraction.bytecode.UnaryOperatorExecutor )
 *   - layout of the stack (type size comes into play)
 */
public class LongUnaryOperatorExecutor extends UnaryOperatorExecutor<Long> {

    private static LongUnaryOperatorExecutor instance;

    public static LongUnaryOperatorExecutor getInstance() {
        if (instance == null) {
            instance = new LongUnaryOperatorExecutor();
        }

        return instance;
    }

    @Override
    protected Expression getOperandExpression(StackFrame sf) {
        return getOperandExpression(sf, 1);
    }

    @Override
    protected Long getOperand(StackFrame sf) {
        return sf.peekLong(0);
    }

    @Override
    final protected void storeExpression(Expression result, StackFrame sf) {
        sf.setLongOperandAttr(result);
    }

    @Override
    protected void storeResult(Expression result, StackFrame sf) {
        sf.popLong();

        sf.pushLong(0);
        storeExpression(result, sf);
    }

}
