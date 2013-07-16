package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

public class JPFInstructionAdaptor {

	public static Instruction getStandardNextInstruction(gov.nasa.jpf.vm.Instruction curInsn, ThreadInfo curTh)
	{
		if (curInsn instanceof gov.nasa.jpf.jvm.bytecode.INVOKESTATIC)
		{
			return ((gov.nasa.jpf.jvm.bytecode.INVOKESTATIC) curInsn).getInvokedMethod(curTh).getFirstInsn();
		}

		if (curInsn instanceof gov.nasa.jpf.jvm.bytecode.INVOKESPECIAL)
		{
			return ((gov.nasa.jpf.jvm.bytecode.INVOKESPECIAL) curInsn).getInvokedMethod(curTh).getFirstInsn();
		}

		if (curInsn instanceof gov.nasa.jpf.jvm.bytecode.VirtualInvocation)
		{
			gov.nasa.jpf.jvm.bytecode.VirtualInvocation invokeInsn = (gov.nasa.jpf.jvm.bytecode.VirtualInvocation) curInsn;

			return invokeInsn.getInvokedMethod(curTh, curTh.getCalleeThis(invokeInsn.getArgSize())).getFirstInsn();
		}

		if (curInsn instanceof gov.nasa.jpf.jvm.bytecode.DIRECTCALLRETURN)
		{
			return curTh.getCallerStackFrame().getPC();
		}

		if (curInsn instanceof gov.nasa.jpf.jvm.bytecode.ReturnInstruction)
		{
			return curTh.getCallerStackFrame().getPC().getNext();
		}

		// default case
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

}
