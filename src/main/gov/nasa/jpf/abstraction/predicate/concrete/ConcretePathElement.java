package gov.nasa.jpf.abstraction.predicate.concrete;

import java.util.Map;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.predicate.common.AccessPathElement;
import gov.nasa.jpf.vm.ThreadInfo;

public interface ConcretePathElement extends AccessPathElement {
	public Map<AccessPath, VariableID> getVariableIDs(ThreadInfo ti);
}
