package gov.nasa.jpf.abstraction.concrete.access.impl;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.meta.Field;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultField;
import gov.nasa.jpf.abstraction.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.concrete.PartialClassID;
import gov.nasa.jpf.abstraction.concrete.PartialVariableID;
import gov.nasa.jpf.abstraction.concrete.Reference;
import gov.nasa.jpf.abstraction.concrete.VariableID;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteAccessExpression;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteObjectFieldWrite;
import gov.nasa.jpf.abstraction.concrete.impl.PathResolution;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

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
	public ConcreteAccessExpression getObject() {
		return (ConcreteAccessExpression) super.getObject();
	}

	@Override
	public PathResolution partialResolve() {
		PathResolution resolution = getObject().partialResolve();
		Map<AccessExpression, VariableID> processed = resolution.processed;
		Map<AccessExpression, VariableID> current = new HashMap<AccessExpression, VariableID>();
		
		for (AccessExpression expr : parents.keySet()) {
			VariableID var = parents.get(expr);
			
			if (var instanceof PartialVariableID) {
				ElementInfo ei = ((PartialVariableID) var).getRef().getElementInfo();
				Object object = ei.getFieldValueObject(getName());
				
				if (var instanceof PartialClassID) {
					PartialClassID classID = (PartialClassID) var;
					
					if (classID.complete()) {
				        if (object instanceof ElementInfo) {
				                // STATIC OBJECT FIELD
				
				        resolution.put(expr, new PartialVariableID(DefaultConcreteAccessExpression.createStaticFieldReference(ti, getName(), ei)));
					} else {
					        // STATIC PRIMITIVE FIELD
					
					                ret.put(path, new StaticFieldID(ei.getClassInfo().getName(), getName()));
					        }
					} else {
					        // NOT YET COMPLETE PATH package.package.Class
					                classID.extend(getName());
					
					                ret.put(path, var);
					        }
					} else if (object instanceof ElementInfo) {
					        // STRUCTURED FIELD (PATH NOT YET COMPLETE)
					        
					        ret.put(path, new PartialVariableID(DefaultConcretePathElement.createObjectFieldReference(ti, getName(), ei)));
					} else {
					        // PRIMITIVE FIELD
					        
					        ret.put(path, new ObjectFieldID(ei.getObjectRef(), getName()));
					}
				}
			}
		}
		
		resolution.processed.putAll(current);
		
		
		return resolution;
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
		return create(newPrefix, getField().clone(), getNewValue().clone());
	}

}
