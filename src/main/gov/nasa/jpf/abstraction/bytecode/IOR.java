package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

public class IOR extends gov.nasa.jpf.jvm.bytecode.IOR {

	@Override
	public Instruction execute (SystemState ss, KernelState ks, ThreadInfo th) {

		StackFrame sf = th.getTopFrame();
		Abstraction abs_v1 = (Abstraction) sf.getOperandAttr(0);
		Abstraction abs_v2 = (Abstraction) sf.getOperandAttr(1);
		if(abs_v1==null && abs_v2==null)
			return super.execute(ss, ks, th);
		else {
			int v1 = th.pop();
			int v2 = th.pop();

			Abstraction result = Abstraction._or(v1, abs_v1, v2, abs_v2);

			if(result.isTop()) {
				System.err.println("non det choice ...");
			}

			th.push(0, false);
			sf.setOperandAttr(result);

			System.err.println("Execute IOR: "+result);

			return getNext(th);
		}
	}	
	
}