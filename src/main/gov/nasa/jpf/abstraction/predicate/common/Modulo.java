package gov.nasa.jpf.abstraction.predicate.common;

public class Modulo extends Subtract {
	public Modulo(Expression a, Expression b) {
		super(a, new Multiply(new Divide(a, b), b));
	}
	
	@Override
	public String toString(AccessPath.NotationPolicy policy) {
		return "(" + a.toString(policy) + " % " + b.toString(policy) + ")";
	}
}