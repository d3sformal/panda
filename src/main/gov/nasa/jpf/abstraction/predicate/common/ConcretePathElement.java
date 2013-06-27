package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.vm.ClassInfo;

public interface ConcretePathElement extends AccessPathElement {
	public ClassInfo getClassInfo();
}
