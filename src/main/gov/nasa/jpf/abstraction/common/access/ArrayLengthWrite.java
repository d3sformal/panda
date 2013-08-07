package gov.nasa.jpf.abstraction.common.access;

import java.util.List;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.UpdateExpression;

public class ArrayLengthWrite extends ArrayLengthExpression implements UpdateExpression {
	
	private Expression newValue;

	protected ArrayLengthWrite(AccessExpression array, Expression newValue) {
		super(array);
		
		this.newValue = newValue;
	}
	
	@Override
	public Expression getNewValue() {
		return newValue;
	}
	
	public static ArrayLengthWrite create(AccessExpression array, Expression newValue) {
		if (array == null || newValue == null) {
			return null;
		}
		
		return new ArrayLengthWrite(array, newValue);
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
	public ArrayLengthWrite clone() {
		return create(getArray().clone(), newValue.clone());
	}

}
