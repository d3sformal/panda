package gov.nasa.jpf.abstraction.common.access;

import java.util.List;

import gov.nasa.jpf.abstraction.common.Expression;

public abstract class ArrayElementExpression extends ArrayExpression {
	
	private Expression index;

	protected ArrayElementExpression(AccessExpression array, Expression index) {
		super(array);
		
		this.index = index;
	}
	
	public Expression getIndex() {
		return index;
	}
	
	@Override
	public List<AccessExpression> getSubAccessExpressions() {
		List<AccessExpression> ret = super.getSubAccessExpressions();
		
		ret.addAll(index.getAccessExpressions());
		
		return ret;
	}
}
