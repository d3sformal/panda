//
//Copyright (C) 2012 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
package gov.nasa.jpf.abstraction.numeric;

/**
 * Abstraction for boolean values.
 * Used as a return value of comparisons.
 */
public class AbstractBoolean {
	public static AbstractBoolean TRUE = new AbstractBoolean();
	public static AbstractBoolean FALSE = new AbstractBoolean();
	public static AbstractBoolean TOP = new AbstractBoolean();

	public AbstractBoolean () {
	}	

	AbstractBoolean abstract_map(boolean v) {
		return (v ? AbstractBoolean.TRUE : AbstractBoolean.FALSE);
	}
	
	/**
	 * @return Negation of this AbstractBoolean
	 */
	public AbstractBoolean not() {
		return create(this != TRUE, this != FALSE);
	}

	/**
	 * @return Conjunction of this AbstractBoolean and the operand.
	 */	
	public AbstractBoolean and(AbstractBoolean right) {
		boolean t = (this != FALSE && right != FALSE);
		boolean f = (this != TRUE || right != TRUE);
		return create(t, f);
	}
	
	/**
	 * @return Disjunction of this AbstractBoolean and the operand.
	 */		
	public AbstractBoolean or(AbstractBoolean right) {
		boolean t = (this != FALSE || right != FALSE);
		boolean f = (this != TRUE && right != TRUE);
		return create(t, f);
	}
	
	/**
	 * @return Exclusive disjunction of this AbstractBoolean and the operand.
	 */		
	public AbstractBoolean xor(AbstractBoolean right) {
		boolean t = (this != FALSE && right != TRUE) || (this != TRUE && right != FALSE);
		boolean f = (this != FALSE && right != FALSE) || (this != TRUE && right != TRUE);
		return create(t, f);
	}	
	
	/**
	 * @param t Indicates whether a new AbstractBoolean can be TRUE
	 * @param f Indicates whether a new AbstractBoolean can be FALSE
	 * @return AbstractBoolean.TRUE, if (t && !f); AbstractBoolean.FALSE, if (!t
	 *         && f); AbstractBoolean.TOP, if (t && f); otherwise throws
	 *         RuntimeException.
	 */		
	public static AbstractBoolean create(boolean t, boolean f) {
		if (t)
			if (f)
				return TOP;
			else
				return TRUE;
		else if (f)
			return FALSE;
		throw new RuntimeException("### Error: AbstractBoolean out of range");
	}
	
	@Override
	public String toString() {
		if (this == TRUE)
			return "TRUE";
		if (this == FALSE)
			return "FALSE";
		if (this == TOP)
			return "TOP";
		return "OutOfRange";
	}
	
}
