package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.vm.ElementInfo;

public abstract class Reference {
	private ElementInfo ei;
	
	public Reference(ElementInfo ei) {
		this.ei = ei;
	}
	
	public ElementInfo getElementInfo() {
		return ei;
	}
	
	public int getObjectRef() {
		return ei.getObjectRef();
	}
	
	@Override
	public String toString() {
		return ((Integer)getObjectRef()).toString();
	}

}
