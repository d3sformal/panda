package gov.nasa.jpf.abstraction.concrete.access.impl;

import java.util.Map;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayLengthWrite;
import gov.nasa.jpf.abstraction.common.access.meta.ArrayLengths;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrayLengths;
import gov.nasa.jpf.abstraction.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.concrete.VariableID;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteAccessExpression;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteArrayLengthExpression;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteArrayLengthWrite;

public class DefaultConcreteArrayLengthWrite extends DefaultArrayLengthWrite implements ConcreteArrayLengthWrite, ConcreteArrayLengthExpression {

	protected DefaultConcreteArrayLengthWrite(ConcreteAccessExpression array, Expression newValue) {
		this(array, DefaultArrayLengths.create(), newValue);
	}
	
	protected DefaultConcreteArrayLengthWrite(ConcreteAccessExpression array, ArrayLengths arrayLengths, Expression newValue) {
		super(array, arrayLengths, newValue);
	}
	
	public static DefaultConcreteArrayLengthWrite create(ConcreteAccessExpression array, Expression newValue) {
		if (array == null || newValue == null) {
			return null;
		}
		
		return new DefaultConcreteArrayLengthWrite(array, newValue);
	}
	
	public static DefaultConcreteArrayLengthWrite create(ConcreteAccessExpression array, ArrayLengths arrayLengths, Expression newValue) {
		if (array == null || arrayLengths == null || newValue == null) {
			return null;
		}
		
		return new DefaultConcreteArrayLengthWrite(array, arrayLengths, newValue);
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
		return create(newPrefix, getArrayLengths().clone(), getNewValue().clone());
	}
}
