package gov.nasa.jpf.abstraction.common;


public class Divide extends Operation {
	public Divide(Expression a, Expression b) {
		super(a, b);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Divide replace(AccessPath formerPath, Expression expression) {
		return new Divide(a.replace(formerPath, expression), b.replace(formerPath, expression));
	}
}
