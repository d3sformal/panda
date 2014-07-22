package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;

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
    protected Expression getFirstOperandExpression(StackFrame sf) {
        return getOperandExpression(sf, 1);
    }

    @Override
    protected Expression getSecondOperandExpression(StackFrame sf) {
        return getOperandExpression(sf, 3);
    }

    @Override
    final protected Long getFirstOperand(StackFrame sf) {
        return sf.peekLong(0);
    }

    @Override
    final protected Long getSecondOperand(StackFrame sf) {
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
