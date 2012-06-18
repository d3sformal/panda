package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.Types;
import gov.nasa.jpf.jvm.bytecode.Instruction;

public class FNEG extends gov.nasa.jpf.jvm.bytecode.FNEG {

	public Instruction execute(SystemState ss, KernelState ks, ThreadInfo th) {

		StackFrame sf = th.getTopFrame();
		Abstraction abs_val = (Abstraction) sf.getOperandAttr(0);
		if (abs_val == null)
			return super.execute(ss, ks, th);
		else {
			float val = Types.intToFloat(th.pop()); // just to pop it

			Abstraction result = Abstraction._neg(abs_val);

			if (result.isTop()) {
				System.out.println("non det choice ...");
			}

			th.push(0, false);
			sf.setOperandAttr(result);

			System.out.println("Execute FNEG: " + result);

			return getNext(th);
		}
	}	
	
}
