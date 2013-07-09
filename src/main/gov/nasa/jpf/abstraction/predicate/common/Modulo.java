package gov.nasa.jpf.abstraction.predicate.common;

public class Modulo extends Subtract {
	public Modulo(Expression a, Expression b) {
		super(a, new Multiply(new Divide(a, b), b));
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}
}