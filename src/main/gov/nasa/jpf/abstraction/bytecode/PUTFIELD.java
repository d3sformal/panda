package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;

public class PUTFIELD extends gov.nasa.jpf.jvm.bytecode.PUTFIELD {

    public PUTFIELD(String fieldName, String classType, String fieldDescriptor) {
        super(fieldName, classType, fieldDescriptor);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        StackFrame sf = ti.getTopFrame();
        Expression from = null;
        AccessExpression to = null;
        ElementInfo ei = null;

        FieldInfo fi = getFieldInfo();

        switch (fi.getTypeCode()) {
            case Types.T_ARRAY:
            case Types.T_REFERENCE:
            case Types.T_BOOLEAN:
            case Types.T_BYTE:
            case Types.T_CHAR:
            case Types.T_SHORT:
            case Types.T_INT:
            case Types.T_FLOAT:
                from = ExpressionUtil.getExpression(sf.getOperandAttr());
                to = ExpressionUtil.getAccessExpression(sf.getOperandAttr(1));
                ei = ti.getModifiableElementInfo(sf.peek(1));
                break;

            case Types.T_LONG:
            case Types.T_DOUBLE:
                from = ExpressionUtil.getExpression(sf.getLongOperandAttr());
                to = ExpressionUtil.getAccessExpression(sf.getOperandAttr(2));
                ei = ti.getModifiableElementInfo(sf.peek(2));
                break;

            default:
        }

        AccessExpression field = DefaultObjectFieldRead.create(to, getFieldName());

        ei.setFieldAttr(fi, from);

        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);

        Instruction actualNextInsn = super.execute(ti);

        if (JPFInstructionAdaptor.testFieldInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        if (ei.getFieldValueObject(getFieldName()) == null || ei.getFieldValueObject(getFieldName()) instanceof ElementInfo) {
            PredicateAbstraction.getInstance().processObjectStore(getMethodInfo(), getPosition(), actualNextInsn.getMethodInfo(), actualNextInsn.getPosition(), from, field);
        } else {
            PredicateAbstraction.getInstance().processPrimitiveStore(getMethodInfo(), getPosition(), actualNextInsn.getMethodInfo(), actualNextInsn.getPosition(), from, field);
        }

        AnonymousExpressionTracker.notifyPopped(from);
        AnonymousExpressionTracker.notifyPopped(to);

        return actualNextInsn;
    }
}
