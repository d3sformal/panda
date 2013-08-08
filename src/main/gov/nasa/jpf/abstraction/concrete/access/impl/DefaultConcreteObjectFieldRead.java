package gov.nasa.jpf.abstraction.concrete.access.impl;

import java.util.Map;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.meta.Field;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultField;
import gov.nasa.jpf.abstraction.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.concrete.VariableID;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteAccessExpression;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteObjectFieldRead;

public class DefaultConcreteObjectFieldRead extends DefaultObjectFieldRead implements ConcreteObjectFieldRead {

	protected DefaultConcreteObjectFieldRead(ConcreteAccessExpression object, String name) {
		this(object, DefaultField.create(name));
	}
	
	protected DefaultConcreteObjectFieldRead(ConcreteAccessExpression object, Field field) {
		super(object, field);
	}
	
	public static DefaultConcreteObjectFieldRead create(ConcreteAccessExpression object, String name) {
		if (object == null || name == null) {
			return null;
		}
		
		return new DefaultConcreteObjectFieldRead(object, name);
	}
	
	public static DefaultConcreteObjectFieldRead create(ConcreteAccessExpression object, Field field) {
		if (object == null || field == null) {
			return null;
		}
		
		return new DefaultConcreteObjectFieldRead(object, field);
	}
	
	@Override
	public Map<AccessExpression, VariableID> partialResolve() {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}

	@Override
	public Map<AccessExpression, VariableID> partialExhaustiveResolve() {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}

	@Override
	public Map<AccessExpression, CompleteVariableID> resolve() {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}
	
	@Override
	public AccessExpression reRoot(AccessExpression newPrefix) {
		return create(newPrefix, getField().clone());
	}

}
