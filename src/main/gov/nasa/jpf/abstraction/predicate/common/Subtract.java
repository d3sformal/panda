package gov.nasa.jpf.abstraction.predicate.common;


public class Subtract extends Operation {
	public Subtract(Expression a, Expression b) {
		super(a, b);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Subtract replace(AccessPath formerPath, Expression expression) {
		return new Subtract(a.replace(formerPath, expression), b.replace(formerPath, expression));
	}
}
