package gov.nasa.jpf.abstraction.predicate.concrete;

import gov.nasa.jpf.abstraction.predicate.grammar.AccessPathRootElement;

public interface ConcretePathRootElement extends AccessPathRootElement, ConcretePathElement {
	public ConcretePath.Type getType();
}
