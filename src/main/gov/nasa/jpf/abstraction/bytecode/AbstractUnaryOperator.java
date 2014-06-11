package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Interface for all unary operations.
 *
 * @see gov.nasa.jpf.abstraction.bytecode.DNEG
 * @see gov.nasa.jpf.abstraction.bytecode.FNEG
 * @see gov.nasa.jpf.abstraction.bytecode.INEG
 * @see gov.nasa.jpf.abstraction.bytecode.LNEG
 */
public interface AbstractUnaryOperator<T> {
    public Instruction executeConcrete(ThreadInfo ti);

    public Expression getResult(Expression expr);

    public Instruction getSelf();
    public Instruction getNext(ThreadInfo ti);
}
