package gov.nasa.jpf.abstraction.common.impl;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.AccessPathElement;
import gov.nasa.jpf.abstraction.common.AccessPathMiddleElement;
import gov.nasa.jpf.abstraction.common.Expression;

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
	
	@Override
	public abstract DefaultAccessPathMiddleElement replace(AccessPath formerPath, Expression expression);
}
