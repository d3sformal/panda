package gov.nasa.jpf.abstraction.common;

import java.util.Map;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;

/**
 * Add is a symbolic expression for arithmetic summation of two variable (e.g. a + b)
 */
public class Add extends Operation {
	protected Add(Expression a, Expression b) {
		super(a, b);
	}

	@Override
	public void accept(PredicatesComponentVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Add replace(Map<AccessExpression, Expression> replacements) {
		Expression newA = a.replace(replacements);
		Expression newB = b.replace(replacements);

		if (newA == a && newB == b) return this;
		else return new Add(newA, newB);
	}
	
	/**
	 * Checked creation of the symbolic expression.
	 * 
	 * No matter the validity of the arguments (e.g. being null) this method is responsible for coping with it
	 * 
	 * @param a left hand side operand
	 * @param b right hand side operand
	 * @return symbolic expression representing the summation / undefined expression / null
	 */
	public static Operation create(Expression a, Expression b) {
		if (!argumentsDefined(a, b)) return null;
		
		if (a instanceof Undefined) return UndefinedOperationResult.create();
		if (b instanceof Undefined) return UndefinedOperationResult.create();
		
		return new Add(a, b);
	}
	
	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		Expression newA = a.update(expression, newExpression);
		Expression newB = b.update(expression, newExpression);

		if (newA == a && newB == b) return this;
		else return create(newA, newB);
	}
}
