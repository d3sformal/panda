package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.common.Add;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Multiply;
import gov.nasa.jpf.abstraction.common.Subtract;

/**
 * Or long
 * ..., value1, value2 => ..., result
 */
public class LOR extends gov.nasa.jpf.jvm.bytecode.LOR implements AbstractBinaryOperator<Long, Long> {

    LongBinaryOperatorExecutor executor = LongBinaryOperatorExecutor.getInstance();

    @Override
    public Instruction execute(ThreadInfo ti) {

        /**
         * Delegates the call to a shared object that does all the heavy lifting
         */
        return executor.execute(this, ti);
    }

    @Override
    public Expression getResult(Expression a, Expression b) {
        LogicalOperandChecker.check(a, b);

        /**
         * Performs the adequate operation over abstractions
         */
        // Assume input values to be either 0 or 1 (logical)
        // Other values will result in errors (bitwise)
        //
        // Therefore:
        // ADD(a, b) - MUL(a, b) = OR(a, b)
        return Subtract.create(Add.create(a, b), Multiply.create(a, b));
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
