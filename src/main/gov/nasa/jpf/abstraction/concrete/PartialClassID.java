package gov.nasa.jpf.abstraction.concrete;

public class PartialClassID extends PartialVariableID {
	
	String path;

	public PartialClassID(Reference ref, String root) {
		super(ref);
		
		path = root;
	}
	
	public boolean complete() {
		return getRef().getElementInfo().getClassInfo().getName().equals(path);
	}
	
	public void extend(String name) {
		path += "." + name;
	}

}
