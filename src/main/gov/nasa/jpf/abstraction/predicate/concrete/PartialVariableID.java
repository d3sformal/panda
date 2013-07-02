package gov.nasa.jpf.abstraction.predicate.concrete;

import gov.nasa.jpf.vm.ElementInfo;

public class PartialVariableID extends VariableID {
	private ElementInfo info;
	
	public PartialVariableID(ElementInfo info) {
		this.info = info;
	}
	
	public ElementInfo getInfo() {
		return info;
	}
}
