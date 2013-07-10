package gov.nasa.jpf.abstraction.predicate.common;

public interface AccessPathElement extends Cloneable, PredicatesVisitable {
	public AccessPathMiddleElement getNext();
	public void setNext(AccessPathMiddleElement next);
	public AccessPathElement clone();
}
