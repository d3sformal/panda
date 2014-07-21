package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.common.Add;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Multiply;
import gov.nasa.jpf.abstraction.common.Subtract;

/**
 * Xor integer
 * ..., value1, value2 => ..., result
 */
public class IXOR extends gov.nasa.jpf.jvm.bytecode.IXOR implements AbstractBinaryOperator<Integer, Integer> {

    IntegerBinaryOperatorExecutor executor = IntegerBinaryOperatorExecutor.getInstance();

    @Override
    public Instruction execute(ThreadInfo ti) {

        /**
         * Delegates the call to a shared object that does all the heavy lifting
         */
        return executor.execute(this, ti);
    }

    @Override
    public Expression getResult(Expression a, Expression b) {
        LogicalOperandChecker.check(getPosition(), a, b);

        /**
         * Performs the adequate operation over abstractions
         */
        // Assume input values to be either 0 or 1 (logical)
        // Other values will result in errors (bitwise)
        //
        // Therefore:
        // ADD(MUL(a, 1 - b), MUL(1 - a, b)) = XOR(a, b)
        // ADD(a, b) - 2 * MUL(a, b) = XOR(a, b)
        return Subtract.create(Add.create(a, b), Multiply.create(Constant.create(2), Multiply.create(a, b)));
    }

    @Override
    public Instruction executeConcrete(ThreadInfo ti) {

        /**
         * Ensures execution of the original instruction
         */
        return super.execute(ti);
    }

    @Override
    public Instruction getSelf() {

        /**
         * Ensures translation into an ordinary instruction
         */
        return this;
    }

}
