package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.MethodFrameSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.NativeStackFrame;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;

public class NATIVERETURN extends gov.nasa.jpf.jvm.bytecode.NATIVERETURN {

    @Override
    public Instruction execute(ThreadInfo ti) {

        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);
        NativeStackFrame before = (NativeStackFrame) ti.getModifiableTopFrame();
        Object retValue = before.getReturnValue();

        // Push the value onto the stack
        // no matter it is native, we need to have an attribute on the stack to return it in processMethodReturn
        switch (before.getMethodInfo().getReturnTypeCode()) {
            case Types.T_ARRAY:
            case Types.T_REFERENCE:
                AnonymousObject returnValue = AnonymousObject.create(new Reference(ti.getElementInfo(((Integer) retValue).intValue())));

                PredicateAbstraction.getInstance().processNewObject(returnValue);

                before.setReturnAttr(returnValue);
                break;

            case Types.T_BOOLEAN:
            case Types.T_BYTE:
            case Types.T_CHAR:
            case Types.T_SHORT:
            case Types.T_INT:
            case Types.T_FLOAT:
            case Types.T_LONG:
            case Types.T_DOUBLE:
                before.setReturnAttr(MethodFrameSymbolTable.DUMMY_VARIABLE);
                break;

            case Types.T_VOID:
            default:
        }

        Instruction actualNextInsn = super.execute(ti);

        StackFrame after = ti.getTopFrame();

        if (JPFInstructionAdaptor.testReturnInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        if (before.getMethodInfo().getReturnTypeCode() == Types.T_VOID) {
            PredicateAbstraction.getInstance().processVoidMethodReturn(ti, before, after);
        } else {
            PredicateAbstraction.getInstance().processMethodReturn(ti, before, after);
        }

        return actualNextInsn;
    }
}
