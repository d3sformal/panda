package gov.nasa.jpf.abstraction.concrete.access.impl;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayLengthRead;
import gov.nasa.jpf.abstraction.common.access.meta.ArrayLengths;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrayLengths;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteAccessExpression;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteArrayLengthRead;
import gov.nasa.jpf.abstraction.concrete.impl.PathResolution;

public class DefaultConcreteArrayLengthRead extends DefaultArrayLengthRead implements ConcreteArrayLengthRead {

	protected DefaultConcreteArrayLengthRead(ConcreteAccessExpression array) {
		this(array, DefaultArrayLengths.create());
	}
	
	protected DefaultConcreteArrayLengthRead(ConcreteAccessExpression array, ArrayLengths arrayLengths) {
		super(array, arrayLengths);
	}
	
	public static DefaultConcreteArrayLengthRead create(ConcreteAccessExpression array) {
		if (array == null) {
			return null;
		}
		
		return new DefaultConcreteArrayLengthRead(array);
	}
	
	public static DefaultConcreteArrayLengthRead create(ConcreteAccessExpression array, ArrayLengths arrayLengths) {
		if (array == null || arrayLengths == null) {
			return null;
		}
		
		return new DefaultConcreteArrayLengthRead(array, arrayLengths);
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
		return create(newPrefix, getArrayLengths().clone());
	}
}
