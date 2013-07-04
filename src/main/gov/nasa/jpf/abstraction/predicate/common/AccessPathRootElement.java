package gov.nasa.jpf.abstraction.predicate.common;

public interface AccessPathRootElement extends AccessPathElement {
	public String getName();
	
	@Override
	public AccessPathRootElement clone();
}
