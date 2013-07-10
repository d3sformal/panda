package gov.nasa.jpf.abstraction.predicate.common;

public class Multiply extends Operation {
	public Multiply(Expression a, Expression b) {
		super(a, b);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}
}
