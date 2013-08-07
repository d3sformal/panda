package gov.nasa.jpf.abstraction.common.access;

import java.util.List;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ObjectExpression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

public class ObjectAccessExpression extends ObjectExpression {
	
	private AccessExpression expression;
	
	protected ObjectAccessExpression(AccessExpression expression) {
		this.expression = expression;
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		expression.accept(visitor);
	}

	@Override
	public List<AccessExpression> getAccessExpressions() {
		return expression.getAccessExpressions();
	}

	@Override
	public Expression replace(AccessExpression formerPath, Expression expression) {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}

	@Override
	public Expression clone() {
		return create(expression.clone());
	}
	
	public static ObjectAccessExpression create(AccessExpression expression) {
		if (expression == null) {
			return null;
		}
		
		return new ObjectAccessExpression(expression);
	}

}
