package gov.nasa.jpf.abstraction.common.access.impl;

import gov.nasa.jpf.abstraction.common.access.ReturnValue;
import gov.nasa.jpf.vm.Instruction;

/**
 * A grammar element representing a keyword 'return' used in predicates over return values of methods
 */
public class DefaultReturnValue extends DefaultRoot implements ReturnValue {
	
	private boolean isReference = false;
	
	protected DefaultReturnValue() {
		super("return");
	}
	
	protected DefaultReturnValue(String name, boolean isReference) {
		super(name);
		
		this.isReference = isReference;
	}
	
	public static DefaultReturnValue create() {
		return new DefaultReturnValue();
	}
	
	public static DefaultReturnValue create(String name, boolean isReference) {
		return new DefaultReturnValue(name, isReference);
	}

	public static DefaultReturnValue create(Instruction pc, boolean isReference) {
		return create("return_pc" + pc.getInstructionIndex(), isReference);
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

    @Override
    public DefaultReturnValue createShallowCopy() {
        return this;
    }
}
