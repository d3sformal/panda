package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

public class I2L extends gov.nasa.jpf.jvm.bytecode.I2L {

	public Instruction execute(SystemState ss, KernelState ks, ThreadInfo th) {
		StackFrame sf = th.getTopFrame();
		Abstraction abs_val = (Abstraction) sf.getOperandAttr(0);		
		
		if (abs_val == null)
			return super.execute(ss, ks, th);
		else {
			int val = th.pop(); // just to pop it
			th.longPush(0);
			sf.setOperandAttr(abs_val);

			System.out.println("Execute I2L: " + abs_val);

			return getNext(th);
		}		
	}

}
