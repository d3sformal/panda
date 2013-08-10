package gov.nasa.jpf.abstraction.common.impl;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.NotationPolicy;

public abstract class DefaultExpression implements Expression {

	@Override
	public final String toString() {
		return toString(NotationPolicy.policy);
	}

	@Override
	public final String toString(NotationPolicy policy) {
		return NotationPolicy.convertToString(this, policy);
	}
	
	@Override
	public abstract DefaultExpression clone();

}
