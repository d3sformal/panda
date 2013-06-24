package gov.nasa.jpf.abstraction.predicate.common;

public class PathFieldElement extends PathMiddleElement {
	public String name;
	
	public PathFieldElement(PathElement previous, String name) {
		super(previous);

		this.name = name;
	}
	
	@Override
	public String toString() {
		String ret = "." + name;
		
		if (next != null) {
			ret += next.toString();
		}
		
		return ret;
	}
}
