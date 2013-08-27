package gov.nasa.jpf.abstraction.concrete.access.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.meta.Field;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultField;
import gov.nasa.jpf.abstraction.concrete.Reference;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteAccessExpression;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteObjectFieldRead;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

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
	public ConcreteAccessExpression getObject() {
		return (ConcreteAccessExpression) super.getObject();
	}
	
	@Override
	public AccessExpression reRoot(AccessExpression newPrefix) {
		return create(newPrefix, getField().clone());
	}

}
