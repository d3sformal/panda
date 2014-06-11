package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Compare floats
 * ..., value1, value2 => ..., result
 */
public class FCMPG extends gov.nasa.jpf.jvm.bytecode.FCMPG implements AbstractBinaryOperator<Float> {

    /**
     * Common implementation of choice generation etc.
     */
    FloatComparatorExecutor executor = FloatComparatorExecutor.getInstance();

    @Override
    public Instruction execute(ThreadInfo ti) {

        /**
         * Delegates the call to a shared object that does all the heavy lifting
         */
        return executor.execute(this, ti);
    }

    @Override
    public Expression getResult(Expression expr1, Expression expr2) {
        return null;
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
