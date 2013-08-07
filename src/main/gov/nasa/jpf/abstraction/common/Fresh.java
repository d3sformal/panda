package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.impl.FreshRootElement;

public class Fresh extends AccessPath {
	
	public Fresh() {
		initialise(FreshRootElement.name);
	}
	
	@Override
	public AccessPathRootElement createRootElement(String name) {
		return new FreshRootElement();
	}
}