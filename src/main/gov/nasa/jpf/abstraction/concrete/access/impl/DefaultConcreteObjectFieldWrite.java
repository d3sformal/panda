package gov.nasa.jpf.abstraction.concrete.access.impl;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.meta.Field;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultField;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteAccessExpression;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteObjectFieldWrite;
import gov.nasa.jpf.abstraction.concrete.impl.PathResolution;

public class DefaultConcreteObjectFieldWrite extends DefaultObjectFieldWrite implements ConcreteObjectFieldWrite {
	
	protected DefaultConcreteObjectFieldWrite(ConcreteAccessExpression object, String name, Expression newValue) {
		this(object, DefaultField.create(name), newValue);
	}
	
	protected DefaultConcreteObjectFieldWrite(ConcreteAccessExpression object, Field field, Expression newValue) {
		super(object, field, newValue);
	}
	
	public static DefaultConcreteObjectFieldWrite create(ConcreteAccessExpression object, String name, Expression newValue) {
		if (object == null || name == null || newValue == null) {
			return null;
		}
		
		return new DefaultConcreteObjectFieldWrite(object, name, newValue);
	}
	
	public static DefaultConcreteObjectFieldWrite create(ConcreteAccessExpression object, Field field, Expression newValue) {
		if (object == null || field == null || newValue == null) {
			return null;
		}
		
		return new DefaultConcreteObjectFieldWrite(object, field, newValue);
	}
	
	@Override
	public AccessExpression reRoot(AccessExpression newPrefix) {
		return create(newPrefix, getField().clone(), getNewValue().clone());
	}

	@Override
	public PathResolution partialResolve() {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}

	@Override
	public PathResolution partialExhaustiveResolve() {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}

	@Override
	public PathResolution resolve() {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}

}
