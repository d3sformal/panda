package gov.nasa.jpf.abstraction.predicate.common;

public interface AccessPathElement {
	public AccessPathMiddleElement getNext();
	public void setNext(AccessPathMiddleElement next);
}
