package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.access.meta.ArrayLengths;

public interface ArrayLengthExpression extends ArrayAccessExpression {
	public ArrayLengths getArrayLengths();
}
