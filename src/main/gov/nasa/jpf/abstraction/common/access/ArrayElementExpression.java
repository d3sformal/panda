package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;

public interface ArrayElementExpression extends ArrayAccessExpression {
	public Expression getIndex();
	public Arrays getArrays();
}
