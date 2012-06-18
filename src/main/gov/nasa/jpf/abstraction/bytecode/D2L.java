package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.Types;
import gov.nasa.jpf.jvm.bytecode.Instruction;

public class D2L extends gov.nasa.jpf.jvm.bytecode.D2L {

	public Instruction execute(SystemState ss, KernelState ks, ThreadInfo th) {
		StackFrame sf = th.getTopFrame();
		Abstraction abs_val = (Abstraction) sf.getLongOperandAttr();

		if (abs_val == null)
			return super.execute(ss, ks, th);
		else {
			double val = Types.longToDouble(th.longPop()); // just to pop it
			th.longPush(0);
			sf.setLongOperandAttr(abs_val);

			System.out.println("Execute D2L: " + abs_val);

			return getNext(th);
		}
	}	
	
}