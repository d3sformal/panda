package gov.nasa.jpf.abstraction.predicate.grammar.impl;

import gov.nasa.jpf.abstraction.predicate.grammar.AccessPathElement;
import gov.nasa.jpf.abstraction.predicate.grammar.AccessPathMiddleElement;

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
	public abstract DefaultAccessPathMiddleElement clone();

}
