package gov.nasa.jpf.abstraction.common;

public interface AccessPathSubElement extends AccessPathMiddleElement {
	public String getName();
	
	@Override
	public AccessPathSubElement clone();
}