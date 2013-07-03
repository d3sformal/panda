package gov.nasa.jpf.abstraction.predicate.grammar;

public class Divide extends Operation {
	public Divide(Expression a, Expression b) {
		super(a, b);
	}
	
	@Override
	public String toString(AccessPath.NotationPolicy policy) {
		return "(" + a.toString(policy) + " / " + b.toString(policy) + ")";
	}
}
