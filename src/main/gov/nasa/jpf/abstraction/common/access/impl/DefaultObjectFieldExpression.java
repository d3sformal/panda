package gov.nasa.jpf.abstraction.common.access.impl;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldExpression;
import gov.nasa.jpf.abstraction.common.access.meta.Field;

public abstract class DefaultObjectFieldExpression extends DefaultObjectAccessExpression implements ObjectFieldExpression {

	private Field field;
	
	protected DefaultObjectFieldExpression(AccessExpression object, Field field) {
		super(object);
		this.field = field;
	}
	
	@Override
	public Field getField() {
		return field;
	}
	
	@Override
	public abstract DefaultObjectFieldExpression clone();

}
