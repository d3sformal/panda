package gov.nasa.jpf.abstraction.concrete;

public class ArrayElementID extends CompleteVariableID {

	private int objRef;
	private int index;
	
	public ArrayElementID(int objRef, int index) {	
		this.objRef = objRef;
		this.index = index;
	}
	
	@Override
	public int hashCode() {
		return objRef + index;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ArrayElementID) {
			ArrayElementID id = (ArrayElementID) o;
			
			return objRef == id.objRef && index == id.index;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "ref(" + objRef + ")[" + index + "]";
	}
}
