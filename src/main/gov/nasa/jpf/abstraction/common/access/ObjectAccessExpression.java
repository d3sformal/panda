package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.ObjectExpression;

/**
 * Expressions accessing objects (fields, array elements, array length):
 * 
 * o.f
 * a[i]
 * a.length
 */
public interface ObjectAccessExpression extends AccessExpression, ObjectExpression {
	public void setObject(AccessExpression expression);
	public AccessExpression getObject();
	
	@Override
	public ObjectAccessExpression clone();
}
