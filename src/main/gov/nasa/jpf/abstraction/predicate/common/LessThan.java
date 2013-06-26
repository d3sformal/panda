package gov.nasa.jpf.abstraction.predicate.common;

public class LessThan extends Comparison {
	public LessThan(Expression a, Expression b) {
		super(a, b);
	}
	
	@Override
	public String toString() {
		return a.toString() + " < " + b.toString();
	}
}
