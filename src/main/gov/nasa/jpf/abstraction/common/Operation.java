package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.impl.DefaultPrimitiveExpression;
import gov.nasa.jpf.abstraction.common.Disjunction;
import gov.nasa.jpf.abstraction.common.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * A common ancestor for +, -, *, /, % ... (all binary operations over symbolic expressions)
 */
public abstract class Operation extends DefaultPrimitiveExpression {
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
	
	@Override
	public Predicate preconditionForBeingFresh() {
		return Disjunction.create(a.preconditionForBeingFresh(), b.preconditionForBeingFresh());
	}
}
