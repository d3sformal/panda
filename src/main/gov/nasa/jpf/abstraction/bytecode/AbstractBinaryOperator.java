package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Interface for all binary arithmetic operations, comparisons
 */
public interface AbstractBinaryOperator<T> {
    public Instruction executeConcrete(ThreadInfo ti);

    public Expression getResult(Expression expr1, Expression expr2);

    public Instruction getSelf();
    public Instruction getNext(ThreadInfo ti);
}
