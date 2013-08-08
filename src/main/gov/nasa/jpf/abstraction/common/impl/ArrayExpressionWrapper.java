package gov.nasa.jpf.abstraction.common.impl;

import gov.nasa.jpf.abstraction.common.Expression;

public class ArrayExpressionWrapper extends ObjectExpressionWrapper {

	protected ArrayExpressionWrapper(Expression expression) {
		super(expression);
	}
	
	public static ArrayExpressionWrapper create(Expression expression) {
		if (expression == null) {
			return null;
		}
		
		return new ArrayExpressionWrapper(expression);
	}
}
