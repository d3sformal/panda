package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

import java.util.List;

/**
 * Corresponds to one object section in the input file
 * 
 * [object ...]
 * b = a - 1
 * a * b = 6
 * ...
 * 
 * <<< SOME OTHER SECTION OR EOF
 */
public class ObjectContext extends Context {
	
	private PackageAndClass packageAndClass;

	public ObjectContext(PackageAndClass packageAndClass, List<Predicate> predicates) {
		super(predicates);
		
		this.packageAndClass = packageAndClass;
	}
	
	public PackageAndClass getPackageAndClass() {
		return packageAndClass;
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}	
}
