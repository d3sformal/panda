package gov.nasa.jpf.abstraction.concrete;



public class LocalVariableID extends CompleteVariableID {
	private int index;
	private String name;

	public LocalVariableID(String name, int index) {
		this.name = name;
		this.index = index;
	}
	
	@Override
	public int hashCode() {
		return index;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof LocalVariableID) {
			LocalVariableID id = (LocalVariableID) o;
			
			return index == id.index && name.equals(id.name);
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "local " + index + ": " + name;
	}
}
