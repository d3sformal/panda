package gov.nasa.jpf.abstraction.common.access.impl;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.Method;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;

/**
 * A grammar element used to specify a target of a [method ...] context
 * 
 * @see gov.nasa.jpf.abstraction.common.MethodContext
 */
public class DefaultMethod implements Method {
	
	private PackageAndClass packageAndClass;
	private String name;
	
	protected DefaultMethod(PackageAndClass packageAndClass, String name) {
		this.packageAndClass = packageAndClass;
		this.name = name;
	}
	
	@Override
	public void accept(PredicatesComponentVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public PackageAndClass getPackageAndClass() {
		return packageAndClass;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return packageAndClass.toString() + "." + getName();
	}
	
	public static DefaultMethod create(PackageAndClass packageAndClass, String name) {
		if (packageAndClass == null || name == null) {
			return null;
		}
		
		return new DefaultMethod(packageAndClass, name);
	}

}
