package gov.nasa.jpf.abstraction.predicate.common;

public class PathIndexElement extends PathMiddleElement {
	public Expression index;
	
	public PathIndexElement(PathElement previous, Expression index) {
		super(previous);
		
		this.index = index;
	}
	
	@Override
	public String toString() {
		String ret = "[" + index.toString() + "]";
		
		if (next != null) {
			ret += next.toString();
		}
		
		return ret;
	}
}
