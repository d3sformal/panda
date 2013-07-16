package gov.nasa.jpf.abstraction.common.impl;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.AccessPathElement;
import gov.nasa.jpf.abstraction.common.AccessPathMiddleElement;
import gov.nasa.jpf.abstraction.common.Expression;

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
	
	@Override
	public abstract DefaultAccessPathElement replace(AccessPath formerPath, Expression expression);
}