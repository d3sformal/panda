package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.ArrayExpression;

/**
 * Access expressions specific to arrays (element/length read: a[0], a.length)
 */
public interface ArrayAccessExpression extends ObjectAccessExpression, ArrayExpression {
	public AccessExpression getArray();
	
	@Override
	public ArrayAccessExpression clone();
}
