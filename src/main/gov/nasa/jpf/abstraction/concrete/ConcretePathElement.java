package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.abstraction.common.AccessPathElement;
import gov.nasa.jpf.abstraction.concrete.impl.PathResolution;
import gov.nasa.jpf.vm.ThreadInfo;

public interface ConcretePathElement extends AccessPathElement {
	public PathResolution getVariableIDs(ThreadInfo ti);
}
