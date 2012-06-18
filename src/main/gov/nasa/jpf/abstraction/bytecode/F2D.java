package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.Types;
import gov.nasa.jpf.jvm.bytecode.Instruction;

public class F2D extends gov.nasa.jpf.jvm.bytecode.F2D {

	public Instruction execute(SystemState ss, KernelState ks, ThreadInfo th) {
		StackFrame sf = th.getTopFrame();
		Abstraction abs_val = (Abstraction) sf.getOperandAttr();

		if (abs_val == null)
			return super.execute(ss, ks, th);
		else {
			float val = Types.intToFloat(th.pop()); // just to pop it
			th.longPush(0);
			sf.setLongOperandAttr(abs_val);

			System.out.println("Execute F2D: " + abs_val);

			return getNext(th);
		}
	}		
	
}
