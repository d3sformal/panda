package gov.nasa.jpf.abstraction.predicate.common;

public abstract class Operation extends Expression {
	public Expression a;
	public Expression b;
	
	public Operation(Expression a, Expression b) {
		this.a = a;
		this.b = b;
	}
}
