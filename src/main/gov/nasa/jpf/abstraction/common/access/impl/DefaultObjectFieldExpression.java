package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.List;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldExpression;
import gov.nasa.jpf.abstraction.common.access.meta.Field;

/**
 * Expressions accessing object fields: fread(f, o); fwrite(f, o, e);
 */
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
	
	@Override
	public List<AccessExpression> getSubAccessExpressions() {
		List<AccessExpression> ret = super.getSubAccessExpressions();
		
		ret.addAll(field.getSubAccessExpressions());
		
		return ret;
	}
}
