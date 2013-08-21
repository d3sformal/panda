package gov.nasa.jpf.abstraction.concrete.access.impl;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementWrite;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteAccessExpression;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteArrayElementWrite;
import gov.nasa.jpf.abstraction.concrete.impl.PathResolution;

public class DefaultConcreteArrayElementWrite extends DefaultArrayElementWrite implements ConcreteArrayElementWrite {

	protected DefaultConcreteArrayElementWrite(ConcreteAccessExpression array, Arrays arrays, Expression index, Expression newValue) {
		super(array, arrays, index, newValue);
	}
	
	protected DefaultConcreteArrayElementWrite(ConcreteAccessExpression array, Expression index, Expression newValue) {
		super(array, index, newValue);
	}
	
	public static DefaultConcreteArrayElementWrite create(ConcreteAccessExpression array, Expression index, Expression newValue) {
		if (array == null || index == null || newValue == null) {
			return null;
		}
		
		return new DefaultConcreteArrayElementWrite(array, index, newValue);
	}
	
	public static DefaultConcreteArrayElementWrite create(ConcreteAccessExpression array, Arrays arrays, Expression index, Expression newValue) {
		if (array == null || arrays == null || index == null || newValue == null) {
			return null;
		}
		
		return new DefaultConcreteArrayElementWrite(array, arrays, index, newValue);
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
	
	@Override
	public AccessExpression reRoot(AccessExpression newPrefix) {		
		return create(newPrefix, getArrays().clone(), getIndex().clone(), getNewValue().clone());
	}
}