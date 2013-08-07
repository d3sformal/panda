package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayAccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectAccessExpression;
import gov.nasa.jpf.abstraction.predicate.state.SymbolTable;

public abstract class ObjectExpression extends Expression {
	
	public static ObjectExpression wrap(Expression expression, SymbolTable symbols) {
		if (expression instanceof ObjectExpression) {
			return (ObjectExpression) expression;
		}
		
		if (expression instanceof AccessExpression) {
			AccessExpression path = (AccessExpression) expression;
			
			if (symbols.isArray(path)) {
				return ArrayAccessExpression.create(path);
			}

			return ObjectAccessExpression.create(path);
		}
		
		throw new RuntimeException("Invalid cast to Object Expression " + expression.getClass().getSimpleName());
	}

}
