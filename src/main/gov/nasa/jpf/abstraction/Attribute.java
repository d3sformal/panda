package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.abstraction.predicate.common.AccessPath;
import gov.nasa.jpf.abstraction.predicate.common.AccessPathType;
import gov.nasa.jpf.vm.ClassInfo;

public class Attribute {
	public AbstractValue abstractValue;
	public AccessPath accessPath;
	public AccessPathType pathType;
	public ClassInfo rootClass;
	
	public Attribute(AbstractValue value, AccessPath path, ClassInfo root, AccessPathType type) {
		abstractValue = value;
		accessPath = path;
		rootClass = root;
		pathType = type;
	}
}
