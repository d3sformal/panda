package gov.nasa.jpf.abstraction.predicate.grammar;

public interface AccessPathSubElement extends AccessPathMiddleElement {
	public String getName();
	
	@Override
	public AccessPathSubElement clone();
}
