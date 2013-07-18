package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.vm.ElementInfo;

public class PartialClassID extends PartialVariableID {
	
	String path;

	public PartialClassID(ElementInfo info, String root) {
		super(info);
		
		path = root;
	}
	
	public boolean complete() {
		return getInfo().getClassInfo().getName().equals(path);
	}
	
	public void extend(String name) {
		path += "." + name;
	}

}
