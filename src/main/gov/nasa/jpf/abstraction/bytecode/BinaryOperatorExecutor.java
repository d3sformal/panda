package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.common.Expression;

/**
 * Implementation of binary operations regardless their concrete type.
 */
public abstract class BinaryOperatorExecutor<T, U> {

    final public Instruction execute(AbstractBinaryOperator<T, U> op, ThreadInfo ti) {

        String name = op.getClass().getSimpleName();

        SystemState ss = ti.getVM().getSystemState();
        StackFrame sf = ti.getModifiableTopFrame();

        Expression expr1 = getFirstOperandExpression(sf);
        Expression expr2 = getSecondOperandExpression(sf);

        T v1 = getFirstOperand(sf);
        U v2 = getSecondOperand(sf);

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

    protected Expression getOperandExpression(StackFrame sf, int index) {
        return (Expression)sf.getOperandAttr(index);
    }

    abstract protected Expression getFirstOperandExpression(StackFrame sf);
    abstract protected Expression getSecondOperandExpression(StackFrame sf);
    abstract protected T getFirstOperand(StackFrame sf);
    abstract protected U getSecondOperand(StackFrame sf);
    abstract protected void storeExpression(Expression result, StackFrame sf);
}
