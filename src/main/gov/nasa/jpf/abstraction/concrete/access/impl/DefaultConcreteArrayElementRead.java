package gov.nasa.jpf.abstraction.concrete.access.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;
import gov.nasa.jpf.abstraction.concrete.ArrayElementID;
import gov.nasa.jpf.abstraction.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.concrete.PartialVariableID;
import gov.nasa.jpf.abstraction.concrete.Reference;
import gov.nasa.jpf.abstraction.concrete.VariableID;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteAccessExpression;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteArrayElementRead;
import gov.nasa.jpf.abstraction.concrete.impl.PathResolution;
import gov.nasa.jpf.vm.ElementInfo;

public class DefaultConcreteArrayElementRead extends DefaultArrayElementRead implements ConcreteArrayElementRead {

	protected DefaultConcreteArrayElementRead(ConcreteAccessExpression array, Arrays arrays, Expression index) {
		super(array, arrays, index);
	}
	
	protected DefaultConcreteArrayElementRead(ConcreteAccessExpression array, Expression index) {
		super(array, index);
	}
	
	public static DefaultConcreteArrayElementRead create(ConcreteAccessExpression array, Expression index) {
		if (array == null || index == null) {
			return null;
		}
		
		return new DefaultConcreteArrayElementRead(array, index);
	}
	
	public static DefaultConcreteArrayElementRead create(ConcreteAccessExpression array, Arrays arrays, Expression index) {
		if (array == null || arrays == null || index == null) {
			return null;
		}
		
		return new DefaultConcreteArrayElementRead(array, arrays, index);
	}
	
	@Override
	public ConcreteAccessExpression getObject() {
		return (ConcreteAccessExpression) super.getObject();
	}
	
	private PathResolution resolveElement(PathResolution resolution) {
		Map<AccessExpression, VariableID> processed = resolution.current;
		Map<AccessExpression, VariableID> current = new HashMap<AccessExpression, VariableID>();
		
		for (AccessExpression path : processed.keySet()) {
			VariableID var = processed.get(path);
			
			if (var instanceof CompleteVariableID) continue;
			
			Reference ref = ((PartialVariableID)var).getRef();
		        
			if (ref.getElementInfo().getClassInfo().isArray()) {
				for (int i = 0; i < ref.getElementInfo().getArrayFields().arrayLength(); ++i) {
					AccessExpression clone = path.clone();
		                        
					clone = DefaultArrayElementRead.create(clone, Constant.create(i));
		
					if (ref.getElementInfo().getClassInfo().isReferenceArray()) {
						ElementInfo info = resolution.threadInfo.getElementInfo(ref.getElementInfo().getArrayFields().getReferenceValue(i));
						
						if (info != null) {
							current.put(clone, new PartialVariableID(DefaultConcreteAccessExpression.createArrayElementReference(resolution.threadInfo, i, ref.getElementInfo())));
						}
					} else {
						current.put(clone, new ArrayElementID(ref.getObjectRef(), i));
					}
				}
			}
		}
		
		return new PathResolution(resolution.threadInfo, processed, current);
	}
	
	@Override
	public PathResolution partialResolve() {
		PathResolution subResolution = getObject().partialResolve();
		
		PathResolution resolution = resolveElement(subResolution);
		
		subResolution.processed.putAll(resolution.processed);
		subResolution.current = resolution.current;
		
		return subResolution;
	}

	@Override
	public PathResolution partialExhaustiveResolve() {
		PathResolution subResolution = getObject().partialExhaustiveResolve();
		PathResolution resolution = resolveElement(subResolution);
		
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
		return create(newPrefix, getArrays().clone(), getIndex().clone());
	}
}
