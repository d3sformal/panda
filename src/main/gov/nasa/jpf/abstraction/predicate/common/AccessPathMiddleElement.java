package gov.nasa.jpf.abstraction.predicate.common;

public interface AccessPathMiddleElement extends AccessPathElement {
	public AccessPathElement getPrevious();
	public void setPrevious(AccessPathElement previous);
	
	@Override
	public AccessPathMiddleElement clone();
}
