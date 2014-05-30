package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;

/**
 * Read/Write to an array element aread(arr, a, i); awrite(arr, a, i, e);
 */
public interface ArrayElementExpression extends ArrayAccessExpression {
    public Expression getIndex();
    public Arrays getArrays();

    @Override
    public ArrayElementExpression createShallowCopy();
}
