package gov.nasa.jpf.abstraction.predicate.common;

public class AccessPathElement {
	public AccessPathMiddleElement next;

	public AccessPathElement(AccessPathMiddleElement next) {
		this.next = next;
	}
}
