package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * A common ancestor to Conjunction and Disjunction (and Implication)
 * 
 * @see gov.nasa.jpf.abstraction.common.Conjunction, gov.nasa.jpf.abstraction.common.Disjunction, gov.nasa.jpf.abstraction.common.Implication
 */
public abstract class Formula extends Predicate {
	public Predicate a;
	public Predicate b;
	
	protected Formula(Predicate a, Predicate b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public List<AccessExpression> getPaths() {
		List<AccessExpression> ret = new ArrayList<AccessExpression>();
		
		ret.addAll(a.getPaths());
		ret.addAll(b.getPaths());
		
		return ret;
	}
	
	public static boolean argumentsDefined(Predicate a, Predicate b) {
		return a != null && b != null;
	}
}
