package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultPackageAndClass;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.util.ExpressionUtil;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;

public class PUTSTATIC extends gov.nasa.jpf.jvm.bytecode.PUTSTATIC {

    public PUTSTATIC(String fieldName, String classType, String fieldDescriptor) {
        super(fieldName, classType, fieldDescriptor);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        StackFrame sf = ti.getTopFrame();
        Expression from = null;

        ElementInfo ei = getClassInfo().getModifiableStaticElementInfo();
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
                break;

            case Types.T_LONG:
            case Types.T_DOUBLE:
                from = ExpressionUtil.getExpression(sf.getLongOperandAttr());
                break;

            default:
        }

        ei.setFieldAttr(fi, from);

        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);

        Instruction actualNextInsn = super.execute(ti);

        if (JPFInstructionAdaptor.testFieldInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        AccessExpression to = null;

        to = DefaultPackageAndClass.create(getClassName());
        to = DefaultObjectFieldRead.create(to, getFieldName());

        if (ei.getFieldValueObject(getFieldName()) == null) {
            PredicateAbstraction.getInstance().processObjectStore(from, to);
        } else if (ei.getFieldValueObject(getFieldName()) instanceof ElementInfo) {
            PredicateAbstraction.getInstance().processObjectStore(from, to);
        } else {
            PredicateAbstraction.getInstance().processPrimitiveStore(from, to);
        }

        AnonymousExpressionTracker.notifyPopped(from);

        return actualNextInsn;
    }
}
