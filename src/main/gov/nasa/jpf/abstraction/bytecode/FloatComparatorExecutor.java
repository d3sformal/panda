package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;

/**
 * Implements type specific parts of the comparison ( @see gov.nasa.jpf.abstraction.bytecode.BinaryComparatorExecutor )
 *   - layout of operands (and their attributes) on the stack (type size comes into play)
 */
public class FloatComparatorExecutor extends BinaryComparatorExecutor<Float> {

    private static FloatComparatorExecutor instance;

    public static FloatComparatorExecutor getInstance() {
        if (instance == null) {
            instance = new FloatComparatorExecutor();
        }

        return instance;
    }

    @Override
    protected Expression getLeftHandSideExpression(StackFrame sf) {
        return getExpression(sf, 0);
    }

    @Override
    protected Expression getRightHandSideExpression(StackFrame sf) {
        return getExpression(sf, 1);
    }

    @Override
    final protected Float getLeftHandSideOperand(StackFrame sf) {
        return sf.peekFloat(0);
    }

    @Override
    final protected Float getRightHandSideOperand(StackFrame sf) {
        return sf.peekFloat(1);
    }

    @Override
    final protected void storeResult(Expression result, StackFrame sf) {
        sf.popFloat();
        sf.popFloat();

        sf.push(((Constant) result).value.intValue());
        sf.setOperandAttr(result);
    }

}
