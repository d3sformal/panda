package gov.nasa.jpf.abstraction.common;

import java.util.Map;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;


public class Divide extends Operation {
	protected Divide(Expression a, Expression b) {
		super(a, b);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Divide replace(Map<AccessExpression, Expression> replacements) {
		return new Divide(a.replace(replacements), b.replace(replacements));
	}
	
	public static Operation create(Expression a, Expression b) {
		if (!argumentsDefined(a, b)) return null;
		
		if (a instanceof Undefined) return UndefinedOperationResult.create();
		if (b instanceof Undefined) return UndefinedOperationResult.create();
		
		return new Divide(a, b);
	}
	
	@Override
	public Operation clone() {
		return create(a.clone(), b.clone());
	}
	
	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		return create(a.update(expression, newExpression), b.update(expression, newExpression));
	}
}
