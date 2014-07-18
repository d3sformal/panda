package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Multiply;
import gov.nasa.jpf.abstraction.common.UninterpretedShiftLeft;

/**
 * Shift left integer
 * ..., value1, value2 => ..., result
 */
public class ISHL extends gov.nasa.jpf.jvm.bytecode.ISHL implements AbstractBinaryOperator<Integer, Integer> {

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
        Expression e;

        if (b instanceof Constant) {
            e = a;

            for (int i = ((Constant) b).value.intValue() & 0x1F; i > 0; --i) {
                e = Multiply.create(e, Constant.create(2));
            }
        } else {
            e = UninterpretedShiftLeft.create(a, b);
        }

        /**
         * Performs the adequate operation over abstractions
         */
        return e;
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
