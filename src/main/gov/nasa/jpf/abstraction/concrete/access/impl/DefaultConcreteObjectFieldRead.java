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
import gov.nasa.jpf.abstraction.predicate.state.symbols.Array;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Object;
import gov.nasa.jpf.abstraction.predicate.state.symbols.PrimitiveValue;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Value;
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
	public Value resolve() {
		Object resolution = (Object)getObject().resolve();
		
		ThreadInfo ti = resolution.getReference().getThreadInfo();
		ElementInfo ei = resolution.getReference().getElementInfo();
		
		Value value = new Object(new Reference(ti, ei));
		
		if (ei != null) {
			java.lang.Object o = ei.getFieldValueObject(getField().getName());
			
			if (o instanceof ElementInfo) {
				ElementInfo sei = (ElementInfo) o;
				
				if (sei.isArray()) {
					value = new Array(new Reference(ti, sei));
				} else {
					value = new Object(new Reference(ti, sei));
				}
			}
		}
		
		resolution.setField(getField().getName(), value);
		
		return value;
	}
	
	@Override
	public AccessExpression reRoot(AccessExpression newPrefix) {
		return create(newPrefix, getField().clone());
	}

}
