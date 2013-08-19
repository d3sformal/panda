package gov.nasa.jpf.abstraction.common.impl;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.NotationPolicy;
import gov.nasa.jpf.abstraction.common.ObjectExpression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

public class NullExpression implements ObjectExpression {
	
	protected NullExpression() {
	}

	@Override
	public List<AccessExpression> getAccessExpressions() {
		return new ArrayList<AccessExpression>();
	}

	@Override
	public Expression replace(AccessExpression expression, Expression newExpression) {
		return clone();
	}

	@Override
	public String toString(NotationPolicy policy) {
		return NotationPolicy.convertToString(this);
	}

	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		return clone();
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public NullExpression clone() {
		return create();
	}
	
	public static NullExpression create() {
		return new NullExpression();
	}
}
