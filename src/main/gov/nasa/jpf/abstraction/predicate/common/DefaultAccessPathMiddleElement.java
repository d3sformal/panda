package gov.nasa.jpf.abstraction.predicate.common;

public abstract class DefaultAccessPathMiddleElement extends DefaultAccessPathElement implements AccessPathMiddleElement,
		AccessPathElement {

	private AccessPathElement previous;

	@Override
	public AccessPathElement getPrevious() {
		return previous;
	}

	@Override
	public void setPrevious(AccessPathElement previous) {
		this.previous = previous;
	}
	
	@Override
	public abstract Object clone();

}
