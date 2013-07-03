package gov.nasa.jpf.abstraction.predicate.grammar.impl;

import gov.nasa.jpf.abstraction.predicate.grammar.AccessPathElement;
import gov.nasa.jpf.abstraction.predicate.grammar.AccessPathMiddleElement;

public abstract class DefaultAccessPathElement implements AccessPathElement {
	
	private AccessPathMiddleElement next;

	@Override
	public AccessPathMiddleElement getNext() {
		return next;
	}

	@Override
	public void setNext(AccessPathMiddleElement next) {
		this.next = next;
	}
	
	@Override
	public abstract DefaultAccessPathElement clone();

}
