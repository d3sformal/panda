package gov.nasa.jpf.abstraction.predicate.common;

public interface ConcretePathRootElement extends AccessPathRootElement, ConcretePathElement {
	public ConcretePath.Type getType();
}
