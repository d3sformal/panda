package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Divide;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.UninterpretedShiftRight;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Shift right long
 * ..., value1, value2 => ..., result
 */
public class LSHR extends gov.nasa.jpf.jvm.bytecode.LSHR implements AbstractBinaryOperator<Long> {

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
        Expression e;

        if (b instanceof Constant) {
            e = a;

            for (int i = ((Constant) b).value.intValue(); i > 0; --i) {
                e = Divide.create(e, Constant.create(2));
            }
        } else {
            e = UninterpretedShiftRight.create(a, b);
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
