package gov.nasa.jpf.abstraction.predicate.common;

public class Equals extends Comparison {
	public Equals(Expression a, Expression b) {
		super(a, b);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}
}
