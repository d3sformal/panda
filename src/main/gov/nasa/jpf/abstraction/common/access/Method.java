package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.PredicatesVisitable;

public interface Method extends PredicatesVisitable {
	public PackageAndClass getPackageAndClass();
	public String getName();
}
