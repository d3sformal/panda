package gov.nasa.jpf.abstraction.predicate.common;

public class LessThan extends Comparison {
	public LessThan(Expression a, Expression b) {
		super(a, b);
	}
	
	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}
}
