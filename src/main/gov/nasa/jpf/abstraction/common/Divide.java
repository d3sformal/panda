package gov.nasa.jpf.abstraction.common;

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
	public Divide replace(AccessExpression formerPath, Expression expression) {
		return new Divide(a.replace(formerPath, expression), b.replace(formerPath, expression));
	}
	
	public static Divide create(Expression a, Expression b) {
		if (!argumentsDefined(a, b)) return null;
		
		return new Divide(a, b);
	}
	
	@Override
	public Divide clone() {
		return create(a.clone(), b.clone());
	}
}
