package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;

public class Subtract extends Operation {
	protected Subtract(Expression a, Expression b) {
		super(a, b);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Subtract replace(AccessExpression formerPath, Expression expression) {
		return new Subtract(a.replace(formerPath, expression), b.replace(formerPath, expression));
	}
	
	public static Subtract create(Expression a, Expression b) {
		if (!argumentsDefined(a, b)) return null;
		
		return new Subtract(a, b);
	}
	
	@Override
	public Subtract clone() {
		return create(a.clone(), b.clone());
	}
}
