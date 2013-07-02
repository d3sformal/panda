package gov.nasa.jpf.abstraction.predicate.concrete;

import java.util.Map;

import gov.nasa.jpf.abstraction.predicate.grammar.AccessPath;
import gov.nasa.jpf.abstraction.predicate.grammar.AccessPathElement;
import gov.nasa.jpf.vm.ThreadInfo;

public interface ConcretePathElement extends AccessPathElement {
	public Map<AccessPath, VariableID> getVariableID(ThreadInfo ti);
}
