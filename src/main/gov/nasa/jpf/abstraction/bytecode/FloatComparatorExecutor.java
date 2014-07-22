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
    protected Expression getFirstOperandExpression(StackFrame sf) {
        return getOperandExpression(sf, 0);
    }

    @Override
    protected Expression getSecondOperandExpression(StackFrame sf) {
        return getOperandExpression(sf, 1);
    }

    @Override
    final protected Float getFirstOperand(StackFrame sf) {
        return sf.peekFloat(0);
    }

    @Override
    final protected Float getSecondOperand(StackFrame sf) {
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
