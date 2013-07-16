package gov.nasa.jpf.abstraction.common;


public class Multiply extends Operation {
	public Multiply(Expression a, Expression b) {
		super(a, b);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Multiply replace(AccessPath formerPath, Expression expression) {
		return new Multiply(a.replace(formerPath, expression), b.replace(formerPath, expression));
	}
}
