package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

public class LSUB extends gov.nasa.jpf.jvm.bytecode.LSUB {

	@Override
	public Instruction execute(SystemState ss, KernelState ks, ThreadInfo th) {
		StackFrame sf = th.getTopFrame();
		Abstraction abs_v1 = (Abstraction) sf.getOperandAttr(1);
		Abstraction abs_v2 = (Abstraction) sf.getOperandAttr(3);

		if (abs_v1 == null && abs_v2 == null)
			return super.execute(ss, ks, th); // we'll still do the concrete
												// execution
		else {
			long v1 = th.longPop();
			long v2 = th.longPop();

			th.longPush(0); // for abstract execution the concrete value does
							// not matter
			Abstraction result = Abstraction._sub(v1, abs_v1, v2, abs_v2);

			if (result.isTop()) {
				System.out.println("non det choice ...");
			}

			sf.setLongOperandAttr(result);

			System.out.println("Execute LSUB: " + result);

			return getNext(th);
		}
	}		
	
}
