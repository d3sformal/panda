package gov.nasa.jpf.abstraction.common.access.impl;

import gov.nasa.jpf.abstraction.common.access.ReturnValue;

public class DefaultReturnValue extends DefaultRoot implements ReturnValue {

	protected DefaultReturnValue() {
		super("return");
	}
	
	public static DefaultReturnValue create() {
		return new DefaultReturnValue();
	}
}
