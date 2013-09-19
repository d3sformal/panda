package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.access.meta.ArrayLengths;

/**
 * Read/Write to an array length: alength(arrlen, a); alengthupdate(arrlen, a, l);
 */
public interface ArrayLengthExpression extends ArrayAccessExpression {
	public ArrayLengths getArrayLengths();
}
