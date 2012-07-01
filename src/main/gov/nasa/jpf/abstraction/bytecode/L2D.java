package gov.nasa.jpf.abstraction.bytecode;

import sun.security.action.GetLongAction;
import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.abstraction.numeric.FocusAbstractChoiceGenerator;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

public class L2D extends gov.nasa.jpf.jvm.bytecode.L2D {

	public Instruction execute(SystemState ss, KernelState ks, ThreadInfo th) {
		StackFrame sf = th.getTopFrame();
		Abstraction abs_val = (Abstraction) sf.getLongOperandAttr();

		if (abs_val == null)
			return super.execute(ss, ks, th);
		else {
			long val = th.longPop(); // just to pop it
			th.longPush(0);
			sf.setLongOperandAttr(abs_val);

			System.out.printf("Values: %d (%s)\n", val, abs_val);
			System.out.printf("Result: %s\n", sf.getLongOperandAttr());

			return getNext(th);
		}
	}		
	
}
