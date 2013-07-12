package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;

public class Modulo extends Subtract {
	public Modulo(Expression a, Expression b) {
		super(a, new Multiply(new Divide(a, b), b));
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Modulo replace(AccessPath formerPath, Expression expression) {
		return new Modulo(a.replace(formerPath, expression), b.replace(formerPath, expression));
	}
}