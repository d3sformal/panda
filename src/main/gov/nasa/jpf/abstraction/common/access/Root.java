package gov.nasa.jpf.abstraction.common.access;

/**
 * A common ancestor of all symbolic expressions that can stand alone (variables, package-class expression)
 * 
 * this contrasts with expressions such as object field read (@see gov.nasa.jpf.abstraction.common.access.ObjectFieldRead) that are not atomic enough in this sense
 */
public interface Root extends AccessExpression {
	public String getName();	
	
	@Override
	public Root clone();
}
