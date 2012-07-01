package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.Types;
import gov.nasa.jpf.jvm.bytecode.Instruction;

public class L2F extends gov.nasa.jpf.jvm.bytecode.L2F {

	public Instruction execute(SystemState ss, KernelState ks, ThreadInfo th) {
		StackFrame sf = th.getTopFrame();
		Abstraction abs_val = (Abstraction) sf.getLongOperandAttr();

		if (abs_val == null)
			return super.execute(ss, ks, th);
		else {
			long val = th.longPop(); // just to pop it
			th.push(0, false);
			sf.setOperandAttr(abs_val);

			System.out.printf("Values: %d (%s)\n", val, abs_val);
			System.out.printf("Result: %s\n", sf.getOperandAttr());

			return getNext(th);
		}
	}	
	
}
