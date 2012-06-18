package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

public class IAND extends gov.nasa.jpf.jvm.bytecode.IAND {

	@Override
	public Instruction execute(SystemState ss, KernelState ks, ThreadInfo th) {

		StackFrame sf = th.getTopFrame();
		Abstraction abs_v1 = (Abstraction) sf.getOperandAttr(0);
		Abstraction abs_v2 = (Abstraction) sf.getOperandAttr(1);
		if (abs_v1 == null && abs_v2 == null)
			return super.execute(ss, ks, th); // we'll still do the concrete
												// execution
		else {
			int v1 = th.pop();
			int v2 = th.pop();

			Abstraction result = Abstraction._and(v1, abs_v1, v2, abs_v2);

			// if result is TOP we need to introduce a non-deterministic choice
			// to add precision
			if (result.isTop()) {
				System.err.println("non det choice ...");
			}

			th.push(0, false); // for abstract operations, the concrete value
								// does not matter for now
			sf.setOperandAttr(result);

			System.err.println("Execute IAND: " + result);

			return getNext(th);
		}
	}

}
