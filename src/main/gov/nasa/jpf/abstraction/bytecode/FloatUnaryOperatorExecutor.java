package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.vm.StackFrame;

/**
 * Implements type specific parts of the operation ( @see gov.nasa.jpf.abstraction.bytecode.UnaryOperatorExecutor )
 *   - layout of the stack (type size comes into play)
 */
public class FloatUnaryOperatorExecutor extends UnaryOperatorExecutor<Float> {

    private static FloatUnaryOperatorExecutor instance;

    public static FloatUnaryOperatorExecutor getInstance() {
        if (instance == null) {
            instance = new FloatUnaryOperatorExecutor();
        }

        return instance;
    }

    @Override
    protected Expression getExpression(StackFrame sf) {
        return getExpression(sf, 1);
    }


    @Override
    final protected Float getOperand(StackFrame sf) {
        return sf.peekFloat(0);
    }

    @Override
    final protected void storeExpression(Expression result, StackFrame sf) {
        sf.setOperandAttr(result);
    }

    @Override
    final protected void storeResult(Expression result, StackFrame sf) {
        sf.popFloat();

        sf.pushFloat(0);
        storeExpression(result, sf);
    }

}
