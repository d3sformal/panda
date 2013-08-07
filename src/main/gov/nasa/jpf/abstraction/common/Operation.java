package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;

import java.util.ArrayList;
import java.util.List;

public abstract class Operation extends PrimitiveExpression {
	public Expression a;
	public Expression b;
	
	protected Operation(Expression a, Expression b) {
		this.a = a;
		this.b = b;
	}
	
	@Override
	public List<AccessExpression> getAccessExpressions() {
		List<AccessExpression> ret = new ArrayList<AccessExpression>();
		
		ret.addAll(a.getAccessExpressions());
		ret.addAll(b.getAccessExpressions());
		
		return ret;
	}
	
	protected static boolean argumentsDefined(Expression a, Expression b) {
		return a != null && b != null;
	}
}
