package gov.nasa.jpf.abstraction.predicate.common;

public class LessThan extends Comparison {
	public LessThan(Expression a, Expression b) {
		super(a, b);
	}
	
	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public LessThan replace(AccessPath formerPath, Expression expression) {
		return new LessThan(a.replace(formerPath, expression), b.replace(formerPath, expression));
	}
}
