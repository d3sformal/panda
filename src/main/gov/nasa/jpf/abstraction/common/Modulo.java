package gov.nasa.jpf.abstraction.common;


public class Modulo extends Subtract {
	public Expression a;
	public Expression b;
	
	protected Modulo(Expression a, Expression b) {
		super(a, new Multiply(new Divide(a, b), b));
		
		this.a = a;
		this.b = b;
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Modulo replace(AccessPath formerPath, Expression expression) {
		return new Modulo(a.replace(formerPath, expression), b.replace(formerPath, expression));
	}
	
	public static Modulo create(Expression a, Expression b) {
		if (!argumentsDefined(a, b)) return null;
		
		return new Modulo(a, b);
	}
	
	@Override
	public Modulo clone() {
		return create(a.clone(), b.clone());
	}
}