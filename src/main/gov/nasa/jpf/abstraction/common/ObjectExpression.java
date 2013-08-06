package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.predicate.state.SymbolTable;

public abstract class ObjectExpression extends Expression {
	public static ObjectExpression wrap(Expression expression, SymbolTable symbols) {
		if (expression instanceof ObjectExpression) {
			return (ObjectExpression) expression;
		}
		
		if (expression instanceof AccessPath) {
			AccessPath path = (AccessPath) expression;
			
			if (symbols.isArray(path)) {
				return ArrayPath.create(path);
			}

			return ObjectPath.create(path);
		}
		
		throw new RuntimeException("Invalid cast to Object Expression " + expression.getClass().getSimpleName());
	}
}
