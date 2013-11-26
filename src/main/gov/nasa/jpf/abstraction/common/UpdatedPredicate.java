package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

import java.util.List;
import java.util.Map;

/**
 * A wrapper of a predicate whose truth value may depend on a symbolic expression modified by an instruction
 * 
 * It holds the original predicate, the original symbolic expression (field, array element, variable ...) and the new value (constant, arithmetic expression ...)
 */
public class UpdatedPredicate extends Predicate {
	
	private Predicate predicate;
	private AccessExpression expression;
	private Expression newExpression;
	
	protected UpdatedPredicate(Predicate predicate, AccessExpression expression, Expression newExpression) {
		this.predicate = predicate;
		this.expression = expression;
		this.newExpression = newExpression;
	}
	
	public Predicate apply() {
		return predicate.update(expression, newExpression);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public List<AccessExpression> getPaths() {		
		return apply().getPaths();
	}

	@Override
	public Predicate replace(Map<AccessExpression, Expression> replacements) {
		return apply().replace(replacements);
	}

	@Override
	public Predicate update(AccessExpression expression, Expression newExpression) {
		return apply().update(expression, newExpression);
	}
	
	public Predicate getPredicate() {
		return predicate;
	}

	public AccessExpression getExpression() {
		return expression;
	}

	public Expression getNewExpression() {
		return newExpression;
	}
	
	public static UpdatedPredicate create(Predicate predicate, AccessExpression expression, Expression newExpression) {
		if (predicate == null || expression == null || newExpression == null) {
			return null;
		}
		
		return new UpdatedPredicate(predicate, expression, newExpression);
	}

}