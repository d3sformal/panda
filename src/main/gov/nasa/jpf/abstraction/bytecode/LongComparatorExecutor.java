package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.vm.StackFrame;

/**
 * Implements type specific parts of the comparison ( @see gov.nasa.jpf.abstraction.bytecode.BinaryComparatorExecutor )
 *   - layout of operands (and their attributes) on the stack (type size comes into play)
 */
public class LongComparatorExecutor extends BinaryComparatorExecutor<Long> {

    private static LongComparatorExecutor instance;

    public static LongComparatorExecutor getInstance() {
        if (instance == null) {
            instance = new LongComparatorExecutor();
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
    final protected Long getLeftHandSideOperand(StackFrame sf) {
        return sf.peekLong(0);
    }

    @Override
    final protected Long getRightHandSideOperand(StackFrame sf) {
        return sf.peekLong(2);
    }

    @Override
    final protected void storeResult(Expression result, StackFrame sf) {
        sf.popLong();
        sf.popLong();

        sf.push(((Constant) result).value.intValue());
        sf.setOperandAttr(result);
    }

}
