package gov.nasa.jpf.abstraction.predicate.common;

public class Multiply extends Operation {
	public Multiply(Expression a, Expression b) {
		super(a, b);
	}
	
	@Override
	public String toString() {
		return "(" + a.toString() + " * " + b.toString() + ")";
	}
}
