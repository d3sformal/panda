package gov.nasa.jpf.abstraction.predicate.grammar;

public class Equals extends Comparison {
	public Equals(Expression a, Expression b) {
		super(a, b);
	}
	
	@Override
	public String toString() {
		return a.toString() + " = " + b.toString();
	}
}
