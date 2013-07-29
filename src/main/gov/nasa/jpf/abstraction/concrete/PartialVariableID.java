package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.vm.ElementInfo;

public class PartialVariableID extends VariableID {
	private ElementInfo info;
	
	public PartialVariableID(ElementInfo info) {
		this.info = info;
	}
	
	public ElementInfo getInfo() {
		return info;
	}
	
	public String toString() {
		return "ref(" + info.getObjectRef() + ")";
	}
}
