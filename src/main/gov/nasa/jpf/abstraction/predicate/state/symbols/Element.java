package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.lang.Object;

public class Element extends SubSlot {
	
	private int index;

	public Element(int index, Value parent) {
		super(parent);
		
		this.index = index;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Element) {
			Element e = (Element) o;
			
			return index == e.index && getParent().equals(e.getParent());
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return index + getParent().hashCode();
	}
	
	@Override
	public String toString() {
		return getParent() + "[" + index + "]";
	}

}
