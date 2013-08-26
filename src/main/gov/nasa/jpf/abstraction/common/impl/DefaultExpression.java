package gov.nasa.jpf.abstraction.common.impl;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;

public abstract class DefaultExpression implements Expression {

	@Override
	public final String toString() {
		return toString(Notation.policy);
	}

	@Override
	public final String toString(Notation policy) {
		return Notation.convertToString(this, policy);
	}
	
	@Override
	public abstract DefaultExpression clone();

}
