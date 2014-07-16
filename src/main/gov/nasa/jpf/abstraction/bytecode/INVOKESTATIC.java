package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Invocation of a static method
 */
public class INVOKESTATIC extends gov.nasa.jpf.jvm.bytecode.INVOKESTATIC {

    public INVOKESTATIC(String clsName, String methodName, String methodSignature) {
        super(clsName, methodName, methodSignature);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {

        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);
        StackFrame before = ti.getTopFrame();

        Instruction actualNextInsn = super.execute(ti);

        StackFrame after = ti.getTopFrame();

        if (JPFInstructionAdaptor.testInvokeStaticInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        /**
         * Collect current symbolic arguments and store them as attributes of the method
         * this allows predicate abstraction to reason about argument assignment
         *
         * These copies of attributes are preserved during the execution of the method and may be used after return.
         * We cannot rely solely on argument attributes for this reason.
         */
        Expression[] arguments = new Expression[after.getMethodInfo().getNumberOfStackArguments()];

        for (int i = 0; i < after.getMethodInfo().getNumberOfStackArguments(); ++i) {
            arguments[after.getMethodInfo().getNumberOfStackArguments() - i - 1] = ExpressionUtil.getExpression(before.getOperandAttr(i));
        }

        after.setFrameAttr(arguments);

        /**
         * Inform abstractions about the method call
         */
        PredicateAbstraction.getInstance().processMethodCall(ti, before, after);

        for (int i = 0; i < after.getMethodInfo().getNumberOfStackArguments(); ++i) {
            AnonymousExpressionTracker.notifyPopped(arguments[i], 1);
        }

        return actualNextInsn;
    }
}
