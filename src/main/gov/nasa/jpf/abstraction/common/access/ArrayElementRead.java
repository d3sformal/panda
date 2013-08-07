package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

public class ArrayElementRead extends ArrayElementExpression {

	protected ArrayElementRead(AccessExpression array, Expression index) {
		super(array, index);
	}
	
	public static ArrayElementRead create(AccessExpression array, Expression index) {
		if (array == null || index == null) {
			return null;
		}
		
		return new ArrayElementRead(array, index);
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
	public ArrayElementRead clone() {
		return create(getArray().clone(), getIndex().clone());
	}

}
