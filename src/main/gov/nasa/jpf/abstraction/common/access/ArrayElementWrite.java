package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.UpdateExpression;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;

public interface ArrayElementWrite extends ArrayElementExpression, UpdateExpression, Arrays {
	@Override
	public ArrayElementWrite clone();
}
