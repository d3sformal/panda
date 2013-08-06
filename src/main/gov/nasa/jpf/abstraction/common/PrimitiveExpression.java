package gov.nasa.jpf.abstraction.common;

public abstract class PrimitiveExpression extends Expression {
	public static PrimitiveExpression wrap(Expression expression) {
		if (expression instanceof PrimitiveExpression) {
			return (PrimitiveExpression) expression;
		}
		
		if (expression instanceof AccessPath) {
			return PrimitivePath.create((AccessPath) expression);
		}
		
		throw new RuntimeException("Invalid cast to Primitive Expression from " + expression.getClass().getSimpleName());
	}
}
