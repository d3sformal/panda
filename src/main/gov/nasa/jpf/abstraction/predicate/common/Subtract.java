package gov.nasa.jpf.abstraction.predicate.common;


public class Subtract extends Operation {
	public Subtract(Expression a, Expression b) {
		super(a, b);
	}
	
	@Override
	public String toString() {
		return "(" + a.toString() + " - " + b.toString() + ")";
	}
}
