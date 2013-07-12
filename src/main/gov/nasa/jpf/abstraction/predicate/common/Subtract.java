package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;


public class Subtract extends Operation {
	public Subtract(Expression a, Expression b) {
		super(a, b);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Subtract replace(AccessPath formerPath, Expression expression) {
		return new Subtract(a.replace(formerPath, expression), b.replace(formerPath, expression));
	}
}
