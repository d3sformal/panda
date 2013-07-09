package gov.nasa.jpf.abstraction.predicate.common;


public class Subtract extends Operation {
	public Subtract(Expression a, Expression b) {
		super(a, b);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}
}
