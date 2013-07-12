package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;

public class Add extends Operation {
	public Add(Expression a, Expression b) {
		super(a, b);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Add replace(AccessPath formerPath, Expression expression) {
		return new Add(a.replace(formerPath, expression), b.replace(formerPath, expression));
	}
}
