package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.UpdateExpression;
import gov.nasa.jpf.abstraction.common.access.meta.ArrayLengths;

public interface ArrayLengthWrite extends ArrayLengthExpression, UpdateExpression, ArrayLengths {
	@Override
	public ArrayLengthWrite clone();
}
