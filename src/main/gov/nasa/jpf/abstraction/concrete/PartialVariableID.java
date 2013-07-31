package gov.nasa.jpf.abstraction.concrete;

public class PartialVariableID extends VariableID {
	private Reference ref;
	
	public PartialVariableID(Reference ref) {
		this.ref = ref;
	}
	
	public Reference getRef() {
		return ref;
	}
	
	public String toString() {
		return "ref(" + ref + ")";
	}
}
