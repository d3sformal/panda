package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;


public interface AccessPathRootElement extends AccessPathElement {
	public String getName();
	
	@Override
	public AccessPathRootElement clone();
	
	@Override
	public AccessPathRootElement replace(AccessPath formerPath, Expression expression);
}
