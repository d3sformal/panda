package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.lang.Object;

public class Field extends SubSlot {
	
	private String name;

	public Field(String name, Value parent) {
		super(parent);
		
		this.name = name;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Field) {
			Field f = (Field) o;
			
			return name.equals(f.name) && getParent().equals(f.getParent());
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode() + getParent().hashCode();
	}
	
	@Override
	public String toString() {
		return getParent() + "." + name;
	}

}
