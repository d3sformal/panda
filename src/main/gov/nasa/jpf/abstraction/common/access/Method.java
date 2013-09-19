package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.PredicatesVisitable;

/**
 * A grammar element used to specify a target of a [method ...] context
 * 
 * @see gov.nasa.jpf.abstraction.common.MethodContext
 */
public interface Method extends PredicatesVisitable {
	public PackageAndClass getPackageAndClass();
	public String getName();
}
