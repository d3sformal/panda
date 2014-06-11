package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.vm.StackFrame;

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
    protected Expression getExpression(StackFrame sf) {
        return getExpression(sf, 1);
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
