package gov.nasa.jpf.abstraction.common.access.impl;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayAccessExpression;

public abstract class DefaultArrayAccessExpression extends DefaultObjectAccessExpression implements ArrayAccessExpression {
	
	protected DefaultArrayAccessExpression(AccessExpression expression) {
		super(expression);
	}

	@Override
	public AccessExpression getArray() {
		return getObject();
	}
	
	@Override
	public abstract DefaultArrayAccessExpression clone();

}
