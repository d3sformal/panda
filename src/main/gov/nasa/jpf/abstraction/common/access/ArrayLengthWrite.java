package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.UpdateExpression;
import gov.nasa.jpf.abstraction.common.access.meta.ArrayLengths;

/**
 * Write to an array length: alengthupdate(arrlen, a, l) ~ a := new int[l]
 */
public interface ArrayLengthWrite extends ArrayLengthExpression, UpdateExpression, ArrayLengths {
    @Override
    public ArrayLengthWrite createShallowCopy();
}
