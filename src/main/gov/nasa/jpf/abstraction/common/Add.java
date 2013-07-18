package gov.nasa.jpf.abstraction.common;


public class Add extends Operation {
	protected Add(Expression a, Expression b) {
		super(a, b);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Add replace(AccessPath formerPath, Expression expression) {
		return new Add(a.replace(formerPath, expression), b.replace(formerPath, expression));
	}
	
	public static Add create(Expression a, Expression b) {
		if (!argumentsDefined(a, b)) return null;
		
		return new Add(a, b);
	}
	
	@Override
	public Add clone() {
		return create(a.clone(), b.clone());
	}
}
