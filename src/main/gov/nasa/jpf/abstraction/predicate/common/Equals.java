package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;

public class Equals extends Comparison {
	public Equals(Expression a, Expression b) {
		super(a, b);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Equals replace(AccessPath formerPath, Expression expression) {
		return new Equals(a.replace(formerPath, expression), b.replace(formerPath, expression));
	}
}
