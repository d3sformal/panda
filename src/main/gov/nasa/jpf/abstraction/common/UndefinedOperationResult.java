package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;

public class UndefinedOperationResult extends Operation implements Undefined {
	
	protected UndefinedOperationResult() {
		super(null, null);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public UndefinedOperationResult clone() {
		return create();
	}
	
	public static UndefinedOperationResult create() {
		return new UndefinedOperationResult();
	}

	@Override
	public Expression replace(AccessExpression expression, Expression newExpression) {
		return clone();
	}

	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		return clone();
	}

}
