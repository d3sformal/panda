package gov.nasa.jpf.abstraction.concrete.access.impl;

import java.util.Map;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;
import gov.nasa.jpf.abstraction.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.concrete.VariableID;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteAccessExpression;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteArrayElementRead;

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
		return create(newPrefix, getArrays().clone(), getIndex().clone());
	}
}
