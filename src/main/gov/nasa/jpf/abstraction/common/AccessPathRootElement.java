package gov.nasa.jpf.abstraction.common;



public interface AccessPathRootElement extends AccessPathElement {
	public String getName();
	
	@Override
	public AccessPathRootElement clone();
	
	@Override
	public AccessPathRootElement replace(AccessPath formerPath, Expression expression);
}