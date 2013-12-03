package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * A common ancestor to all non-constant elemental predicates supported in this project
 * <, =
 * 
 * For constant predicates @see gov.nasa.jpf.abstraction.common.Tautology,gov.nasa.jpf.abstraction.common.Contradiction
 * Also @see gov.nasa.jpf.abstraction.common.Negation 
 */
public abstract class Comparison extends Predicate {
	public Expression a;
	public Expression b;
	
	protected Comparison(Expression a, Expression b) {
		this.a = a;
		this.b = b;
		this.hashCodeValue = toString(Notation.DOT_NOTATION).hashCode();
	}
	
	@Override
	public List<AccessExpression> getPaths() {
		List<AccessExpression> ret = new ArrayList<AccessExpression>();
		
		ret.addAll(a.getAccessExpressions());
		ret.addAll(b.getAccessExpressions());
		
		return ret;
	}
	
	/**
	 * Common check for validating predicates over symbolic expressions. 
	 */
	protected static boolean argumentsDefined(Expression a, Expression b) {
		return a != null && b != null;
	}
}
