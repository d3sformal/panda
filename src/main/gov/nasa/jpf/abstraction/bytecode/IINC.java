package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

public class IINC extends gov.nasa.jpf.jvm.bytecode.IINC {
	
	public IINC(int localVarIndex, int increment){
		super(localVarIndex, increment);
	}	

	public Instruction execute(SystemState ss, KernelState ks, ThreadInfo th) {
		
		StackFrame sf = th.getTopFrame();
		Abstraction abs_v = (Abstraction) sf.getLocalAttr(index);
		if(abs_v == null)
			th.setLocalVariable(index, th.getLocalVariable(index) + increment, false);
		else {
			th.setLocalVariable(index, 0, false);
	    	sf.setLocalAttr(index,abs_v._plus(increment));
			System.out.println("Execute IINC: "+sf.getLocalAttr(index));
		}
		return getNext(th);
	}	

}
