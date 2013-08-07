package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;

public class Multiply extends Operation {
	protected Multiply(Expression a, Expression b) {
		super(a, b);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Multiply replace(AccessExpression formerPath, Expression expression) {
		return new Multiply(a.replace(formerPath, expression), b.replace(formerPath, expression));
	}
	
	public static Multiply create(Expression a, Expression b) {
		if (!argumentsDefined(a, b)) return null;
		
		return new Multiply(a, b);
	}
	
	@Override
	public Multiply clone() {
		return create(a.clone(), b.clone());
	}
}
