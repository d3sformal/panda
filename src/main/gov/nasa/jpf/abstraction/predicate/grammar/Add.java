package gov.nasa.jpf.abstraction.predicate.grammar;

public class Add extends Operation {
	public Add(Expression a, Expression b) {
		super(a, b);
	}
	
	@Override
	public String toString(AccessPath.NotationPolicy policy) {
		return "(" + a.toString(policy) + " + " + b.toString(policy) + ")";
	}
}
