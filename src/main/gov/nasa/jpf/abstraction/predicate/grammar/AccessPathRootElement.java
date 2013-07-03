package gov.nasa.jpf.abstraction.predicate.grammar;

public interface AccessPathRootElement extends AccessPathElement {
	public String getName();
	
	@Override
	public AccessPathRootElement clone();
}
