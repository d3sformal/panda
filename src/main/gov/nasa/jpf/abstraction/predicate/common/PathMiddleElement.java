package gov.nasa.jpf.abstraction.predicate.common;

public class PathMiddleElement extends PathElement {
	public PathElement previous;
	
	public PathMiddleElement(PathElement previous) {
		super(null);

		this.previous = previous;
	}
}
