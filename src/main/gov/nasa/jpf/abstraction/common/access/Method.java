package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitable;

/**
 * A grammar element used to specify a target of a [method ...] context
 *
 * @see gov.nasa.jpf.abstraction.common.MethodContext
 */
public interface Method extends PredicatesComponentVisitable {
    public PackageAndClass getPackageAndClass();
    public String getName();
}
