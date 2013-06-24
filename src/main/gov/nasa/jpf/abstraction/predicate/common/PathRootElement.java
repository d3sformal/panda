package gov.nasa.jpf.abstraction.predicate.common;

public class PathRootElement extends PathElement {
	public String name;
	
	public PathRootElement(String name) {
		super(null);

		this.name = name;
	}
	
	@Override
	public String toString() {
		String ret = name;
		
		if (next != null) {
			ret += next.toString();
		}
		
		return ret;
	}
}
