package gov.nasa.jpf.abstraction.predicate.grammar;

public class Multiply extends Operation {
	public Multiply(Expression a, Expression b) {
		super(a, b);
	}
	
	@Override
	public String toString(AccessPath.NotationPolicy policy) {
		return "(" + a.toString(policy) + " * " + b.toString(policy) + ")";
	}
}
