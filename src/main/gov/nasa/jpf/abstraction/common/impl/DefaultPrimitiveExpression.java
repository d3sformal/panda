package gov.nasa.jpf.abstraction.common.impl;

import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.PrimitiveExpression;

public abstract class DefaultPrimitiveExpression implements PrimitiveExpression {
	@Override
	public final String toString() {
		return toString(Notation.policy);
	}

	@Override
	public final String toString(Notation policy) {
    	return Notation.convertToString(this, policy);
	}
	
	@Override
	public abstract DefaultPrimitiveExpression clone();
}
