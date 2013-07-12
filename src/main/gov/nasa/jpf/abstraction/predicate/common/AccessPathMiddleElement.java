package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;


public interface AccessPathMiddleElement extends AccessPathElement {
	public AccessPathElement getPrevious();
	public void setPrevious(AccessPathElement previous);
	
	@Override
	public AccessPathMiddleElement clone();
	
	@Override
	public AccessPathMiddleElement replace(AccessPath formerPath, Expression expression);
}
