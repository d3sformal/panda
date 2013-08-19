package gov.nasa.jpf.abstraction.concrete.access.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.meta.Field;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultField;
import gov.nasa.jpf.abstraction.concrete.ObjectFieldID;
import gov.nasa.jpf.abstraction.concrete.PartialClassID;
import gov.nasa.jpf.abstraction.concrete.PartialVariableID;
import gov.nasa.jpf.abstraction.concrete.StaticFieldID;
import gov.nasa.jpf.abstraction.concrete.VariableID;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteAccessExpression;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteObjectFieldRead;
import gov.nasa.jpf.abstraction.concrete.impl.PathResolution;
import gov.nasa.jpf.vm.ElementInfo;

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
	
	private PathResolution resolveField(PathResolution resolution) {		
		Map<AccessExpression, VariableID> processed = resolution.current;
		Map<AccessExpression, VariableID> current = new HashMap<AccessExpression, VariableID>();
		
		for (AccessExpression expr : processed.keySet()) {
			VariableID var = processed.get(expr);
			
			if (var instanceof PartialVariableID) {
				ElementInfo ei = ((PartialVariableID) var).getRef().getElementInfo();
				Object object = ei.getFieldValueObject(getField().getName());
				
				if (var instanceof PartialClassID) {
					PartialClassID classID = (PartialClassID) var;
					
					if (classID.complete()) {
						if (object instanceof ElementInfo) {
							// STATIC OBJECT FIELD
				
							current.put(expr, new PartialVariableID(DefaultConcreteAccessExpression.createStaticFieldReference(resolution.threadInfo, getField().getName(), ei)));
						} else {
							// STATIC PRIMITIVE FIELD
					
							current.put(expr, new StaticFieldID(ei.getClassInfo().getName(), getField().getName()));
						}
					} else {
						// NOT YET COMPLETE PATH package.package.Class
						classID.extend(getField().getName());
					
						current.put(expr, var);
					}
				} else if (object instanceof ElementInfo) {
					// STRUCTURED FIELD (PATH NOT YET COMPLETE)
					        
					current.put(expr, new PartialVariableID(DefaultConcreteAccessExpression.createObjectFieldReference(resolution.threadInfo, getField().getName(), ei)));
				} else {
					// PRIMITIVE FIELD
					
					current.put(expr, new ObjectFieldID(ei.getObjectRef(), getField().getName()));
				}
			}
		}
		
		return new PathResolution(resolution.threadInfo, processed, current);
	}
	
	@Override
	public PathResolution partialResolve() {
		PathResolution subResolution = getObject().partialResolve();
		
		PathResolution resolution = resolveField(subResolution);
		
		subResolution.processed.putAll(resolution.processed);
		subResolution.current = resolution.current;
		
		return subResolution;
	}

	@Override
	public PathResolution partialExhaustiveResolve() {
		PathResolution subResolution = getObject().partialExhaustiveResolve();
		PathResolution resolution = resolveField(subResolution);
		
		resolution.processed = new HashMap<AccessExpression, VariableID>();
		resolution.processed.putAll(resolution.current);
		
		return resolution;
	}

	@Override
	public PathResolution resolve() {
		PathResolution resolution = partialExhaustiveResolve();
		Set<AccessExpression> toBeRemoved = new HashSet<AccessExpression>();
		
		for (AccessExpression expr : resolution.processed.keySet()) {
			if (resolution.processed.get(expr) instanceof PartialVariableID) {
				toBeRemoved.add(expr);
			}
		}
		
		for (AccessExpression expr : toBeRemoved) {
			resolution.processed.remove(expr);
		}
		
		return resolution;
	}
	
	@Override
	public AccessExpression reRoot(AccessExpression newPrefix) {
		return create(newPrefix, getField().clone());
	}

}
