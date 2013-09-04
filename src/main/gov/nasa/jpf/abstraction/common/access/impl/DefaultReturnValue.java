package gov.nasa.jpf.abstraction.common.access.impl;

import gov.nasa.jpf.abstraction.common.access.ReturnValue;
import gov.nasa.jpf.vm.Instruction;

public class DefaultReturnValue extends DefaultRoot implements ReturnValue {
	
	private boolean isReference = false;
	
	protected DefaultReturnValue() {
		super("return");
	}
	
	protected DefaultReturnValue(Instruction pc, boolean isReference) {
		super("return_pc" + pc.getInstructionIndex());
		
		this.isReference = isReference;
	}
	
	public static DefaultReturnValue create() {
		return new DefaultReturnValue();
	}
	
	public static DefaultReturnValue create(Instruction pc, boolean isReference) {
		return new DefaultReturnValue(pc, isReference);
	}
	
	@Override
	public boolean isReference() {
		return isReference;
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
