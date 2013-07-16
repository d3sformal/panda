package gov.nasa.jpf.abstraction.common;


public interface AccessPathElement extends Cloneable, PredicatesVisitable {
	public AccessPathMiddleElement getNext();
	public void setNext(AccessPathMiddleElement next);
	public AccessPathElement clone();
	public AccessPathElement replace(AccessPath formerPath, Expression expression);
}
