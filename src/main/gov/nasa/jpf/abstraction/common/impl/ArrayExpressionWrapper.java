package gov.nasa.jpf.abstraction.common.impl;

import gov.nasa.jpf.abstraction.common.ArrayExpression;
import gov.nasa.jpf.abstraction.common.Expression;

/**
 * Wrapper which marks expressions as Array Expressions @see gov.nasa.jpf.abstraction.common.ArrayExpression
 */
public class ArrayExpressionWrapper extends ObjectExpressionWrapper implements ArrayExpression {

	protected ArrayExpressionWrapper(Expression expression) {
		super(expression);
	}
	
	public static ArrayExpressionWrapper create(Expression expression) {
		if (expression == null) {
			return null;
		}
		
		return new ArrayExpressionWrapper(expression);
	}
	
	@Override
	public ArrayExpressionWrapper clone() {
		return create(expression);
	}
}
