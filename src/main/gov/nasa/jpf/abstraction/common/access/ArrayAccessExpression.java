package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.ArrayExpression;

public interface ArrayAccessExpression extends ObjectAccessExpression, ArrayExpression {
	public AccessExpression getArray();
	
	@Override
	public ArrayAccessExpression clone();
}
