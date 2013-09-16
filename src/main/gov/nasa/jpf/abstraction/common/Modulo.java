package gov.nasa.jpf.abstraction.common;

import java.util.Map;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;


public class Modulo extends Subtract {
	public Expression a;
	public Expression b;
	
	protected Modulo(Expression a, Expression b) {
		super(a, new Multiply(new Divide(a, b), b));
		
		this.a = a;
		this.b = b;
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Modulo replace(Map<AccessExpression, Expression> replacements) {
		return new Modulo(a.replace(replacements), b.replace(replacements));
	}
	
	public static Operation create(Expression a, Expression b) {
		if (!argumentsDefined(a, b)) return null;
		
		if (a instanceof Undefined) return UndefinedOperationResult.create();
		if (b instanceof Undefined) return UndefinedOperationResult.create();
		
		return new Modulo(a, b);
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