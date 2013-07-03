package gov.nasa.jpf.abstraction.predicate.grammar;

public class Equals extends Comparison {
	public Equals(Expression a, Expression b) {
		super(a, b);
	}
	
	@Override
	public String toString(AccessPath.NotationPolicy policy) {
		return a.toString(policy) + " = " + b.toString(policy);
	}
}
