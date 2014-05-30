package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.MethodInfo;

/**
 * JPFInstructionAdaptor decouples our implementation of individual instructions from the implementation in jpf-core when it comes to aborting the execution
 */
public class JPFInstructionAdaptor {

    public static Instruction getStandardNextInstruction(gov.nasa.jpf.jvm.bytecode.INVOKESTATIC curInsn, ThreadInfo curTh)
    {
        return curInsn.getInvokedMethod(curTh).getFirstInsn();
    }

    public static Instruction getStandardNextInstruction(gov.nasa.jpf.jvm.bytecode.INVOKESPECIAL curInsn, ThreadInfo curTh)
    {
        return curInsn.getInvokedMethod(curTh).getFirstInsn();
    }

    public static Instruction getStandardNextInstruction(gov.nasa.jpf.jvm.bytecode.VirtualInvocation curInsn, ThreadInfo curTh)
    {
        MethodInfo method = curInsn.getInvokedMethod(curTh, curTh.getCalleeThis(curInsn.getArgSize()));

        // may happen if the target object is null
        // we can return anything because super.execute in INVOKExxx fails anyway
        if (method == null) {
            return curInsn;
        }

        return method.getFirstInsn();
    }

    public static Instruction getStandardNextInstruction(gov.nasa.jpf.jvm.bytecode.DIRECTCALLRETURN curInsn, ThreadInfo curTh)
    {
        if (curTh.getCallerStackFrame() == null) {
            return null;
        }
        return curTh.getCallerStackFrame().getPC();
    }

    public static Instruction getStandardNextInstruction(gov.nasa.jpf.jvm.bytecode.ReturnInstruction curInsn, ThreadInfo curTh)
    {
        return curTh.getCallerStackFrame().getPC().getNext();
    }

    public static Instruction getStandardNextInstruction(gov.nasa.jpf.vm.Instruction curInsn, ThreadInfo curTh)
    {
        return curInsn.getNext(curTh);
    }


    public static boolean testLocalVarInstructionAbort(gov.nasa.jpf.jvm.bytecode.LocalVariableInstruction curInsn, ThreadInfo ti, Instruction expectedNextInsn, Instruction actualNextInsn) {
        return actualNextInsn != expectedNextInsn;
    }

    public static boolean testArrayElementInstructionAbort(gov.nasa.jpf.jvm.bytecode.ArrayElementInstruction curInsn, ThreadInfo ti, Instruction expectedNextInsn, Instruction actualNextInsn) {
        return actualNextInsn != expectedNextInsn;
    }

    public static boolean testFieldInstructionAbort(gov.nasa.jpf.jvm.bytecode.FieldInstruction cur, ThreadInfo ti, Instruction expectedNextInsn, Instruction actualNextInsn) {
        return actualNextInsn != expectedNextInsn;
    }

    public static boolean testReturnInstructionAbort(gov.nasa.jpf.jvm.bytecode.ReturnInstruction curInsn, ThreadInfo ti, Instruction expectedNextInsn, Instruction actualNextInsn) {
        return actualNextInsn == curInsn || actualNextInsn != expectedNextInsn;
    }

    public static boolean testDirectCallReturnInstructionAbort(gov.nasa.jpf.jvm.bytecode.DIRECTCALLRETURN curInsn, ThreadInfo ti, Instruction expectedNextInsn, Instruction actualNextInsn) {
        return actualNextInsn == curInsn || actualNextInsn != expectedNextInsn;
    }

    public static boolean testInvokeSpecialInstructionAbort(gov.nasa.jpf.jvm.bytecode.INVOKESPECIAL curInsn, ThreadInfo ti, Instruction expectedNextInsn, Instruction actualNextInsn) {
        return actualNextInsn == curInsn || actualNextInsn != expectedNextInsn;
    }

    public static boolean testVirtualInvocationInstructionAbort(gov.nasa.jpf.jvm.bytecode.VirtualInvocation curInsn, ThreadInfo ti, Instruction expectedNextInsn, Instruction actualNextInsn) {
        return actualNextInsn == curInsn || actualNextInsn != expectedNextInsn;
    }

    public static boolean testInvokeStaticInstructionAbort(gov.nasa.jpf.jvm.bytecode.INVOKESTATIC curInsn, ThreadInfo ti, Instruction expectedNextInsn, Instruction actualNextInsn) {
        return actualNextInsn == curInsn || actualNextInsn != expectedNextInsn;
    }

    public static boolean testNewArrayInstructionAbort(gov.nasa.jpf.jvm.bytecode.NewArrayInstruction curInsn, ThreadInfo ti, Instruction expectedNextInsn, Instruction actualNextInsn) {
        return actualNextInsn != expectedNextInsn;
    }

    public static boolean testNewArrayInstructionAbort(gov.nasa.jpf.jvm.bytecode.MULTIANEWARRAY curInsn, ThreadInfo ti, Instruction expectedNextInsn, Instruction actualNextInsn) {
        return actualNextInsn != expectedNextInsn;
    }

    public static boolean testNewInstructionAbort(gov.nasa.jpf.jvm.bytecode.NEW curInsn, ThreadInfo ti, Instruction expectedNextInsn, Instruction actualNextInsn) {
        return actualNextInsn != expectedNextInsn;
    }

}
