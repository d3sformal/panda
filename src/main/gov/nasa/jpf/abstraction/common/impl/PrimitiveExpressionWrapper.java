package gov.nasa.jpf.abstraction.common.impl;

import java.util.List;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.PrimitiveExpression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ReturnValue;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.SymbolTable;

public class PrimitiveExpressionWrapper extends DefaultPrimitiveExpression {
	private Expression expression;
	
	public PrimitiveExpressionWrapper(Expression expression) {
		this.expression = expression;
	}

	public static PrimitiveExpression wrap(Expression expression, SymbolTable symbols) {
		if (expression instanceof PrimitiveExpression) {
			return (PrimitiveExpression) expression;
		}
		
		if (expression instanceof ReturnValue) {
			ReturnValue r = (ReturnValue) expression;
			
			if (!r.isReference()) {
				return PrimitiveExpressionWrapper.create(expression);
			}
		}
		if (expression instanceof AccessExpression) {
			AccessExpression path = (AccessExpression) expression;
			
			if (symbols.isPrimitive(path)) {
				return PrimitiveExpressionWrapper.create(expression);
			}
		}
		
		throw new RuntimeException("Invalid cast to Primitive Expression " + expression + " " + expression.getClass().getSimpleName());
	}

	private static PrimitiveExpressionWrapper create(Expression expression) {
		if (expression == null) {
			return null;
		}
		
		return new PrimitiveExpressionWrapper(expression);
	}

	@Override
	public List<AccessExpression> getAccessExpressions() {
		return expression.getAccessExpressions();
	}

	@Override
	public Expression replace(AccessExpression expression, Expression newExpression) {
		return create(this.expression.replace(expression, newExpression));
	}

	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		return create(this.expression.update(expression, newExpression));
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		expression.accept(visitor);
	}

	@Override
	public DefaultPrimitiveExpression clone() {
		return create(expression.clone());
	}
	
	@Override
	public Predicate preconditionForBeingFresh() {
		return expression.preconditionForBeingFresh();
	}
	
	@Override
	public boolean equals(Object o) {
		return expression.equals(o);
	}
	
	@Override
	public int hashCode() {
		return expression.hashCode();
	}
	
}
