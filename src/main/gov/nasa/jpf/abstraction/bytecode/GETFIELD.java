package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.common.impl.NullExpression;

/**
 * Loads a value of a field of an object (identified by objRef) onto the stack
 * A common instruction for all types of fields (primitive, reference)
 */
public class GETFIELD extends gov.nasa.jpf.jvm.bytecode.GETFIELD {

    public GETFIELD(String fieldName, String classType, String fieldDescriptor) {
        super(fieldName, classType, fieldDescriptor);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        StackFrame sf = ti.getTopFrame();
        AccessExpression from = ExpressionUtil.getAccessExpression(sf.getOperandAttr());
        AccessExpression path = DefaultObjectFieldRead.create(from, getFieldName());

        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);

        if (sf.peek() == MJIEnv.NULL) {
            PredicateAbstraction.getInstance().extendTraceFormulaWithConstraint(Equals.create(from, NullExpression.create()), sf.getMethodInfo(), getPosition());
        }

        Instruction actualNextInsn = super.execute(ti);

        /**
         * In case the instruction did not finish correctly do not add any symbols to the stack value
         */
        if (JPFInstructionAdaptor.testFieldInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        sf = ti.getModifiableTopFrame();
        sf.setOperandAttr(path);

        AnonymousExpressionTracker.notifyPopped(from);

        return actualNextInsn;
    }
}
