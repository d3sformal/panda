package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.Types;
import gov.nasa.jpf.jvm.bytecode.Instruction;

public class DNEG extends gov.nasa.jpf.jvm.bytecode.DNEG {

	public Instruction execute(SystemState ss, KernelState ks, ThreadInfo th) {

		StackFrame sf = th.getTopFrame();
		Abstraction abs_val = (Abstraction) sf.getOperandAttr(1);
		if (abs_val == null)
			return super.execute(ss, ks, th);
		else {
			double val = Types.longToDouble(th.longPop()); // just to pop it

			Abstraction result = Abstraction._neg(abs_val);

			if (result.isTop()) {
				System.out.println("non det choice ...");
			}

			th.longPush(0);
			sf.setLongOperandAttr(result);

			System.out.println("Execute DNEG: " + result);

			return getNext(th);
		}
	}
		
	
}
