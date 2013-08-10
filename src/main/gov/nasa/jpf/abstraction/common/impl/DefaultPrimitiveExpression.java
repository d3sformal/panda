package gov.nasa.jpf.abstraction.common.impl;

import gov.nasa.jpf.abstraction.common.NotationPolicy;
import gov.nasa.jpf.abstraction.common.PrimitiveExpression;

public abstract class DefaultPrimitiveExpression implements PrimitiveExpression {
	@Override
	public final String toString() {
		return toString(NotationPolicy.policy);
	}

	@Override
	public final String toString(NotationPolicy policy) {
    	return NotationPolicy.convertToString(this, policy);
	}
	
	@Override
	public abstract DefaultPrimitiveExpression clone();
}
