package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.abstraction.common.AccessPathRootElement;

public interface ConcretePathRootElement extends AccessPathRootElement, ConcretePathElement {
	public ConcretePath.Type getType();
}
