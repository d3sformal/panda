package gov.nasa.jpf.abstraction.predicate.common;

public class ObjectFieldID extends VariableID {
	
	private int objRef;
	private String fieldName;
	
	public ObjectFieldID(int objRef, String fieldName) {
		this.objRef = objRef;
		this.fieldName = fieldName;
	}
	
	@Override
	public int hashCode() {
		return objRef + fieldName.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ObjectFieldID) {
			ObjectFieldID id = (ObjectFieldID) o;
			
			return objRef == id.objRef && fieldName.equals(fieldName);
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "ref(" + objRef + ")." + fieldName;
	}
}
