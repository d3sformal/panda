package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Implementation of binary operations regardless their concrete type.
 */
public abstract class BinaryOperatorExecutor<T> {

    final public Instruction execute(AbstractBinaryOperator<T> op, ThreadInfo ti) {

        String name = op.getClass().getSimpleName();

        SystemState ss = ti.getVM().getSystemState();
        StackFrame sf = ti.getModifiableTopFrame();

        Expression expr1 = getLeftHandSideExpression(sf);
        Expression expr2 = getRightHandSideExpression(sf);

        T v1 = getLeftHandSideOperand(sf);
        T v2 = getRightHandSideOperand(sf);

        // Create symbolic value (predicate abstraction), abstract value (numeric abstraction)
        Expression result;

        try {
            result = op.getResult(expr1, expr2);
        } catch (RuntimeException e) {
            return ti.createAndThrowException(e.getClass().getName(), e.getMessage());
        }

        // Concrete execution
        Instruction ret = op.executeConcrete(ti);

        storeExpression(result, sf);

        return ret;
    }

    protected Expression getExpression(StackFrame sf, int index) {
        return (Expression)sf.getOperandAttr(index);
    }

    abstract protected Expression getLeftHandSideExpression(StackFrame sf);
    abstract protected Expression getRightHandSideExpression(StackFrame sf);
    abstract protected T getLeftHandSideOperand(StackFrame sf);
    abstract protected T getRightHandSideOperand(StackFrame sf);
    abstract protected void storeExpression(Expression result, StackFrame sf);
}
