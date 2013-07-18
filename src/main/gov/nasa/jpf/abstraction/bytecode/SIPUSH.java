package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.impl.NonEmptyAttribute;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class SIPUSH extends gov.nasa.jpf.jvm.bytecode.SIPUSH {
	
	public SIPUSH(int value) {
		super(value);
	}

	@Override
	public Instruction execute(ThreadInfo ti) {
		Instruction ret = super.execute(ti);
		
		StackFrame sf = ti.getModifiableTopFrame();
		sf.setOperandAttr(new NonEmptyAttribute(null, Constant.create(getValue())));
		
		return ret;
	}

}
