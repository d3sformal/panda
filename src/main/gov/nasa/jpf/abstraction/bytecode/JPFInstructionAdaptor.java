package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

public class JPFInstructionAdaptor {
	public static boolean testLocalVarInstructionAbortion(gov.nasa.jpf.jvm.bytecode.LocalVariableInstruction cur, Instruction ret, ThreadInfo ti) {
		return ret != cur.getNext(ti);
	}
	
	public static boolean testArrayElementInstructionAbortion(gov.nasa.jpf.jvm.bytecode.ArrayElementInstruction cur, Instruction ret, ThreadInfo ti) {
		return ret != cur.getNext(ti);
	}
	
	public static boolean testFieldInstructionAbortion(gov.nasa.jpf.jvm.bytecode.FieldInstruction cur, Instruction ret, ThreadInfo ti) {
		return ret != cur.getNext(ti);
	}
	
	public static boolean testReturnInstructionAbortion(gov.nasa.jpf.jvm.bytecode.ReturnInstruction cur, Instruction ret, ThreadInfo ti) {
		return ret == cur || ret != ti.getPC().getNext();
	}
	
	public static boolean testDirectCallReturnInstructionAbortion(gov.nasa.jpf.jvm.bytecode.DIRECTCALLRETURN cur, Instruction ret, ThreadInfo ti) {
		return ret == cur || ret != ti.getPC();
	}

	public static boolean testInvokeSpecialInstructionAbortion(gov.nasa.jpf.jvm.bytecode.INVOKESPECIAL cur, Instruction ret, ThreadInfo ti) {
		return ret == cur || ret != ti.getPC();
	}
	
	public static boolean testVirtualInvocationInstructionAbortion(gov.nasa.jpf.jvm.bytecode.VirtualInvocation cur, Instruction ret, ThreadInfo ti) {
		return ret == cur || ret != ti.getPC();
	}
	
	public static boolean testInvokeStaticInstructionAbortion(gov.nasa.jpf.jvm.bytecode.INVOKESTATIC cur, Instruction ret, ThreadInfo ti) {
		return ret == cur || ret != ti.getPC();
	}
}
