package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.ObjectExpression;

public interface ObjectAccessExpression extends AccessExpression, ObjectExpression {
	public void setObject(AccessExpression expression);
	public AccessExpression getObject();
	
	@Override
	public ObjectAccessExpression clone();
}
