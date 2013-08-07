package gov.nasa.jpf.abstraction.common.access;

import java.util.List;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.UpdateExpression;

public class ObjectFieldWrite extends ObjectFieldExpression implements UpdateExpression {
	
	private Expression newValue;

	protected ObjectFieldWrite(AccessExpression object, String name, Expression newValue) {
		super(object, name);
		
		this.newValue = newValue;
	}
	
	@Override
	public Expression getNewValue() {
		return newValue;
	}
	
	public static ObjectFieldWrite create(AccessExpression object, String name, Expression newValue) {
		if (object == null || name == null || newValue == null) {
			return null;
		}
		
		return new ObjectFieldWrite(object, name, newValue);
	}
	
	@Override
	public List<AccessExpression> getSubAccessExpressions() {
		List<AccessExpression> ret = super.getSubAccessExpressions();
		
		ret.addAll(newValue.getAccessExpressions());
		
		return ret;
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
	public ObjectFieldWrite clone() {
		return create(getObject().clone(), getName(), newValue.clone());
	}

}
