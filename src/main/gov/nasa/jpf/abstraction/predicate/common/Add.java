package gov.nasa.jpf.abstraction.predicate.common;

public class Add extends Operation {
	public Add(Expression a, Expression b) {
		super(a, b);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}
}
