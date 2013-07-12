package gov.nasa.jpf.abstraction.predicate.concrete;

import gov.nasa.jpf.abstraction.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.predicate.common.AccessPathRootElement;

public interface ConcretePathRootElement extends AccessPathRootElement, ConcretePathElement {
	public ConcretePath.Type getType();
}
