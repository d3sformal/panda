package gov.nasa.jpf.abstraction.concrete;

public class PartialClassID extends PartialVariableID {
	
	String packageAndClass;

	public PartialClassID(Reference ref, String packageAndClass) {
		super(ref);
		
		this.packageAndClass = packageAndClass;
	}

}
