package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.vm.StackFrame;

/**
 * Implements type specific parts of the operation ( @see gov.nasa.jpf.abstraction.bytecode.UnaryOperatorExecutor )
 *   - layout of the stack (type size comes into play)
 */
public class IntegerUnaryOperatorExecutor extends UnaryOperatorExecutor<Integer> {

    private static IntegerUnaryOperatorExecutor instance;

    public static IntegerUnaryOperatorExecutor getInstance() {
        if (instance == null) {
            instance = new IntegerUnaryOperatorExecutor();
        }

        return instance;
    }

    @Override
    protected Expression getExpression(StackFrame sf) {
        return getExpression(sf, 0);
    }


    @Override
    final protected Integer getOperand(StackFrame sf) {
        return sf.peek(0);
    }

    @Override
    final protected void storeExpression(Expression result, StackFrame sf) {
        sf.setOperandAttr(result);
    }

    @Override
    final protected void storeResult(Expression result, StackFrame sf) {
        sf.pop();

        sf.push(0);
        storeExpression(result, sf);
    }

}
