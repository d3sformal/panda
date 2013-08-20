package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.List;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthExpression;
import gov.nasa.jpf.abstraction.common.access.meta.ArrayLengths;

public abstract class DefaultArrayLengthExpression extends DefaultArrayAccessExpression implements ArrayLengthExpression {
	
	private ArrayLengths arrayLengths;
	
	protected DefaultArrayLengthExpression(AccessExpression array, ArrayLengths arrayLengths) {
		super(array);
		
		this.arrayLengths = arrayLengths;
	}
	
	@Override
	public ArrayLengths getArrayLengths() {
		return arrayLengths;
	}
	
	@Override
	public List<AccessExpression> getSubAccessExpressions() {
		return getArray().getAccessExpressions();
	}

}
