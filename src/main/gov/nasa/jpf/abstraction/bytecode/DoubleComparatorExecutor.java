package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.vm.StackFrame;

/**
 * Implements type specific parts of the comparison ( @see gov.nasa.jpf.abstraction.bytecode.BinaryComparatorExecutor )
 *   - layout of operands (and their attributes) on the stack (type size comes into play)
 */
public class DoubleComparatorExecutor extends BinaryComparatorExecutor<Double> {

    private static DoubleComparatorExecutor instance;

    public static DoubleComparatorExecutor getInstance() {
        if (instance == null) {
            instance = new DoubleComparatorExecutor();
        }

        return instance;
    }

    @Override
    protected Expression getLeftHandSideExpression(StackFrame sf) {
        return getExpression(sf, 1);
    }

    @Override
    protected Expression getRightHandSideExpression(StackFrame sf) {
        return getExpression(sf, 3);
    }

    @Override
    final protected Double getLeftHandSideOperand(StackFrame sf) {
        return sf.peekDouble(0);
    }

    @Override
    final protected Double getRightHandSideOperand(StackFrame sf) {
        return sf.peekDouble(2);
    }

    @Override
    final protected void storeResult(Expression result, StackFrame sf) {
        sf.popDouble();
        sf.popDouble();

        sf.push(((Constant) result).value.intValue());
        sf.setOperandAttr(result);
    }

}
