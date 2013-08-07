package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

public class ArrayLengthRead extends ArrayLengthExpression {

	protected ArrayLengthRead(AccessExpression array) {
		super(array);
	}
	
	public static ArrayLengthRead create(AccessExpression array) {
		if (array == null) {
			return null;
		}
		
		return new ArrayLengthRead(array);
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
	public ArrayLengthRead clone() {
		return create(getArray().clone());
	}

}
