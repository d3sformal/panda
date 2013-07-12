package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;

public interface AccessPathElement extends Cloneable, PredicatesVisitable {
	public AccessPathMiddleElement getNext();
	public void setNext(AccessPathMiddleElement next);
	public AccessPathElement clone();
	public AccessPathElement replace(AccessPath formerPath, Expression expression);
}
