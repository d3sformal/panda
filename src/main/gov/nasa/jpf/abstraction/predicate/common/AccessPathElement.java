package gov.nasa.jpf.abstraction.predicate.common;

public class AccessPathElement {
	public AccessPathElement next;

	public AccessPathElement(AccessPathElement next) {
		this.next = next;
	}
}
