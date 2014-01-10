package gov.nasa.jpf.abstraction.common.impl;

import java.util.List;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ObjectExpression;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ReturnValue;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.SymbolTable;

/**
 * Wrapper which marks expressions as Object Expressions @see gov.nasa.jpf.abstraction.common.ObjectExpression
 */
public class ObjectExpressionDecorator extends DefaultObjectExpression {
	protected Expression expression;
	
	protected ObjectExpressionDecorator(Expression expression) {
		this.expression = expression;
	}
	
	public static ObjectExpression wrap(Expression expression, SymbolTable symbols) {
		if (expression instanceof ObjectExpression) {
			return (ObjectExpression) expression;
		}
		
		if (expression instanceof AccessExpression) {
			AccessExpression path = (AccessExpression) expression;
			
			if (path instanceof ReturnValue) {
				ReturnValue r = (ReturnValue) path;
				
				if (r.isReference()) {
					return ObjectExpressionDecorator.create(path);
				}
			}
			if (symbols.isObject(path)) {
				if (symbols.isArray(path)) {
					return ArrayExpressionDecorator.create(path);
				}
				
				return ObjectExpressionDecorator.create(path);
			}
		}
		
		throw new RuntimeException("Invalid cast to Object Expression " + expression.getClass().getSimpleName());
	}
	
	public static ObjectExpressionDecorator create(Expression expression) {
		if (expression == null) {
			return null;
		}
		
		return new ObjectExpressionDecorator(expression);
	}

	@Override
	public List<AccessExpression> getAccessExpressions() {
		return expression.getAccessExpressions();
	}
	
	@Override
	public Expression replace(Map<AccessExpression, Expression> replacements) {
		return create(this.expression.replace(replacements));
	}

	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		return create(this.expression.update(expression, newExpression));
	}

	@Override
	public void accept(PredicatesComponentVisitor visitor) {
		expression.accept(visitor);
	}

	@Override
	public DefaultObjectExpression clone() {
		return create(expression.clone());
	}

	@Override
	public Predicate getPreconditionForBeingFresh() {
		return expression.getPreconditionForBeingFresh();
	}
	
	@Override
	public boolean equals(java.lang.Object o) {
		return expression.equals(o);
	}
	
	@Override
	public int hashCode() {
		return expression.hashCode();
	}
}
