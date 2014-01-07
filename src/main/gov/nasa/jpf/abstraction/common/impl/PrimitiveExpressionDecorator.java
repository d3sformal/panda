package gov.nasa.jpf.abstraction.common.impl;

import java.util.List;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.PrimitiveExpression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ReturnValue;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.SymbolTable;

/**
 * Wrapper used for marking expressions as primitive
 */
public class PrimitiveExpressionDecorator extends DefaultPrimitiveExpression {
	private Expression expression;
	
	public PrimitiveExpressionDecorator(Expression expression) {
		this.expression = expression;
	}

	public static PrimitiveExpression wrap(Expression expression, SymbolTable symbols) {
		if (expression instanceof PrimitiveExpression) {
			return (PrimitiveExpression) expression;
		}
		
		if (expression instanceof ReturnValue) {
			ReturnValue r = (ReturnValue) expression;
			
			if (!r.isReference()) {
				return PrimitiveExpressionDecorator.create(expression);
			}
		}
		if (expression instanceof AccessExpression) {
			AccessExpression path = (AccessExpression) expression;
			
			if (symbols.isPrimitive(path)) {
				return PrimitiveExpressionDecorator.create(expression);
			}
            System.out.println("NOT PRIMITIVE 1<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		} else {
		System.out.println("NOT PRIMITIVE 2<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		}
		
		throw new RuntimeException("Invalid cast to Primitive Expression " + expression + " " + expression.getClass().getSimpleName());
	}

	private static PrimitiveExpressionDecorator create(Expression expression) {
		if (expression == null) {
			return null;
		}
		
		return new PrimitiveExpressionDecorator(expression);
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
	public void accept(PredicatesVisitor visitor) {
		expression.accept(visitor);
	}

	@Override
	public DefaultPrimitiveExpression clone() {
		return create(expression.clone());
	}
	
	@Override
	public Predicate getPreconditionForBeingFresh() {
		return expression.getPreconditionForBeingFresh();
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