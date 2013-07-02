package gov.nasa.jpf.abstraction.predicate.grammar;

public class Multiply extends Operation {
	public Multiply(Expression a, Expression b) {
		super(a, b);
	}
	
	@Override
	public String toString() {
		return "(" + a.toString() + " * " + b.toString() + ")";
	}
}
