package gov.nasa.jpf.abstraction.common.impl;

import gov.nasa.jpf.abstraction.common.ArrayExpression;
import gov.nasa.jpf.abstraction.common.Expression;

/**
 * Wrapper which marks expressions as Array Expressions @see gov.nasa.jpf.abstraction.common.ArrayExpression
 */
public class ArrayExpressionDecorator extends ObjectExpressionDecorator implements ArrayExpression {

	protected ArrayExpressionDecorator(Expression expression) {
		super(expression);
	}
	
	public static ArrayExpressionDecorator create(Expression expression) {
		if (expression == null) {
			return null;
		}
		
		return new ArrayExpressionDecorator(expression);
	}
	
	@Override
	public ArrayExpressionDecorator clone() {
		return create(expression);
	}
}
