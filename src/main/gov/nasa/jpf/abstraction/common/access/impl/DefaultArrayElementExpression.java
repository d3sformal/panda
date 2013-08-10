package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.List;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementExpression;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;

public abstract class DefaultArrayElementExpression extends DefaultArrayAccessExpression implements ArrayElementExpression {
	
	private Expression index;
	private Arrays arrays;

	protected DefaultArrayElementExpression(AccessExpression array, Arrays arrays, Expression index) {
		super(array);
		
		this.arrays = arrays;
		this.index = index;
	}
	
	@Override
	public Expression getIndex() {
		return index;
	}
	
	@Override
	public Arrays getArrays() {
		return arrays;
	}
	
	@Override
	public List<AccessExpression> getSubAccessExpressions() {
		List<AccessExpression> ret = super.getSubAccessExpressions();
		
		ret.addAll(index.getAccessExpressions());
		
		return ret;
	}
}
