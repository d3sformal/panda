package gov.nasa.jpf.abstraction.predicate.common;

public abstract class Comparison extends Predicate {
	public Expression a;
	public Expression b;
	
	public Comparison(Expression a, Expression b) {
		this.a = a;
		this.b = b;
	}
}
