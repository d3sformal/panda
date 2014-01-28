package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.UpdateExpression;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;

/**
 * Write to an array element awrite(arr, a, i, e) ~ a[i] := e
 */
public interface ArrayElementWrite extends ArrayElementExpression, UpdateExpression, Arrays {
    @Override
    public ArrayElementWrite createShallowCopy();
}
