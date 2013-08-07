package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

public class ObjectFieldRead extends ObjectFieldExpression {

	protected ObjectFieldRead(AccessExpression object, String name) {
		super(object, name);
	}
	
	public static ObjectFieldRead create(AccessExpression object, String name) {
		if (object == null || name == null) {
			return null;
		}
		
		return new ObjectFieldRead(object, name);
	}
	
	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Expression replace(AccessExpression formerPath, Expression expression) {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}

	@Override
	public ObjectFieldRead clone() {
		return create(getObject().clone(), getName());
	}
	
}
