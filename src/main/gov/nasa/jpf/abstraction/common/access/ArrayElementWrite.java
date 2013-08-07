package gov.nasa.jpf.abstraction.common.access;

import java.util.List;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.UpdateExpression;

public class ArrayElementWrite extends ArrayElementExpression implements UpdateExpression {
	
	private Expression newValue;

	protected ArrayElementWrite(AccessExpression array, Expression index, Expression newValue) {
		super(array, index);
		
		this.newValue = newValue;
	}
	
	@Override
	public Expression getNewValue() {
		return newValue;
	}
	
	public static ArrayElementWrite create(AccessExpression array, Expression index, Expression newValue) {
		if (array == null || index == null || newValue == null) {
			return null;
		}
		
		return new ArrayElementWrite(array, index, newValue);
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
	public ArrayElementWrite clone() {
		return create(getArray().clone(), getIndex().clone(), newValue.clone());
	}

}
