package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

public class Fresh extends Root {

	protected Fresh() {
		super("fresh");
	}
	
	public static Fresh create() {
		return new Fresh();
	}
	
	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public AccessExpression clone() {
		return create(getName());
	}

	@Override
	public Expression replace(AccessExpression formerPath, Expression expression) {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}
}