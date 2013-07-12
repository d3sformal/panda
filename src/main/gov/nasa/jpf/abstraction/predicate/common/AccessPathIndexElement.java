package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.Expression;


public interface AccessPathIndexElement extends AccessPathMiddleElement {
	public Expression getIndex();
	
	@Override
	public AccessPathIndexElement clone();
}
