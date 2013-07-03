package gov.nasa.jpf.abstraction.predicate.grammar;

public interface AccessPathMiddleElement extends AccessPathElement {
	public AccessPathElement getPrevious();
	public void setPrevious(AccessPathElement previous);
	
	@Override
	public AccessPathMiddleElement clone();
}
