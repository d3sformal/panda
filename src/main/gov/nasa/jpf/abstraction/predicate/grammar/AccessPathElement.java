package gov.nasa.jpf.abstraction.predicate.grammar;

public interface AccessPathElement extends Cloneable {
	public AccessPathMiddleElement getNext();
	public void setNext(AccessPathMiddleElement next);
	public AccessPathElement clone();
	public String toString(AccessPath.NotationPolicy policy);
}
