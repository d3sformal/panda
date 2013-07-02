package gov.nasa.jpf.abstraction.predicate.concrete;

import gov.nasa.jpf.abstraction.predicate.grammar.AccessPathElement;
import gov.nasa.jpf.vm.ThreadInfo;

public interface ConcretePathElement extends AccessPathElement {
	public VariableID getVariableID(ThreadInfo ti);
}
