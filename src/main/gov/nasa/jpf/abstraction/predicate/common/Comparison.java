package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;

import java.util.ArrayList;
import java.util.List;

public abstract class Comparison extends Predicate {
	public Expression a;
	public Expression b;
	
	protected Comparison(Expression a, Expression b) {
		this.a = a;
		this.b = b;
	}
	
	@Override
	public List<AccessExpression> getPaths() {
		List<AccessExpression> ret = new ArrayList<AccessExpression>();
		
		ret.addAll(a.getAccessExpressions());
		ret.addAll(b.getAccessExpressions());
		
		return ret;
	}
	
	protected static boolean argumentsDefined(Expression a, Expression b) {
		return a != null && b != null;
	}
}
