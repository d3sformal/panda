package gov.nasa.jpf.abstraction.common;



public interface AccessPathIndexElement extends AccessPathMiddleElement {
	public Expression getIndex();
	
	@Override
	public AccessPathIndexElement clone();
}
