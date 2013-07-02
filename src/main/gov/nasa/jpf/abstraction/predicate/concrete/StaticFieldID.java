package gov.nasa.jpf.abstraction.predicate.concrete;


public class StaticFieldID extends CompleteVariableID {
	private String className;
	private String fieldName;

	public StaticFieldID(String className, String fieldName) {
		this.className = className;
		this.fieldName = fieldName;
	}
	
	@Override
	public int hashCode() {
		return className.hashCode() + fieldName.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof StaticFieldID) {
			StaticFieldID id = (StaticFieldID) o;
			
			return className.equals(id.className) && fieldName.equals(id.fieldName);
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return className + "." + fieldName;
	}
}
