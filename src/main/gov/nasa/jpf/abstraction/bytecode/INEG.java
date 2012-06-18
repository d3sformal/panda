package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InstructionVisitor;

/**
 * Negate int ..., value => ..., result
 */
public class INEG extends gov.nasa.jpf.jvm.bytecode.INEG {

	public Instruction execute(SystemState ss, KernelState ks, ThreadInfo th) {

		StackFrame sf = th.getTopFrame();
		Abstraction abs_val = (Abstraction) sf.getOperandAttr(0);
		if (abs_val == null)
			return super.execute(ss, ks, th);
		else {
			int val = th.pop(); // just to pop it

			Abstraction result = Abstraction._neg(abs_val);

			if (result.isTop()) {
				System.out.println("non det choice ...");
			}

			th.push(0, false);
			sf.setOperandAttr(result);

			System.out.println("Execute INEG: " + result);

			return getNext(th);
		}
	}

}
