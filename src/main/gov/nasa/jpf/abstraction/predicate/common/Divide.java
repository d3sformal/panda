package gov.nasa.jpf.abstraction.predicate.common;

public class Divide extends Operation {
	public Divide(Expression a, Expression b) {
		super(a, b);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}
}
