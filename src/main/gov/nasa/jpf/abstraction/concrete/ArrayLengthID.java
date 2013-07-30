package gov.nasa.jpf.abstraction.concrete;

public class ArrayLengthID extends CompleteVariableID {

	private int objRef;
	
	public ArrayLengthID(int objRef) {	
		this.objRef = objRef;
	}
	
	@Override
	public int hashCode() {
		return objRef;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ArrayLengthID) {
			ArrayLengthID id = (ArrayLengthID) o;
			
			return objRef == id.objRef;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "ref(" + objRef + ").length";
	}
}
