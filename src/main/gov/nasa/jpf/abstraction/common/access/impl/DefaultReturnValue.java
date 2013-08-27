package gov.nasa.jpf.abstraction.common.access.impl;

import gov.nasa.jpf.abstraction.common.access.ReturnValue;
import gov.nasa.jpf.vm.Instruction;

public class DefaultReturnValue extends DefaultRoot implements ReturnValue {
	
	protected DefaultReturnValue() {
		super("return");
	}
	
	protected DefaultReturnValue(Instruction pc) {
		super("return_pc" + pc.getInstructionIndex());
	}
	
	public static DefaultReturnValue create() {
		return new DefaultReturnValue();
	}
	
	public static DefaultReturnValue create(Instruction pc) {
		return new DefaultReturnValue(pc);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ReturnValue) {
			ReturnValue r = (ReturnValue) o;
			
			return getName().equals(r.getName());
		}
		
		return false;
	}
}
