package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.Set;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementExpression;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;

/**
 * Read/Write to an array element aread(arr, a, i); awrite(arr, a, i, e); 
 */
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
	public void addAccessSubExpressionsToSet(Set<AccessExpression> out) {
		super.addAccessSubExpressionsToSet(out);
		
		arrays.addAccessSubExpressionsToSet(out);
		index.addAccessExpressionsToSet(out);
	}
}
