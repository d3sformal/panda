package gov.nasa.jpf.abstraction.predicate.common;

public class Divide extends Operation {
	public Divide(Expression a, Expression b) {
		super(a, b);
	}
	
	@Override
	public String toString() {
		return "(" + a.toString() + " / " + b.toString() + ")";
	}
}
