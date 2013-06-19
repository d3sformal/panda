//
// Copyright (C) 2012 United States Government as represented by the
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

import java.util.Set;

public abstract class AbstractValue {
	protected int key;
	protected Abstraction abs;

	/**
	 * This constructor is here to force all AbstractValues to call set_key. It is
	 * important, because keys are used to distinguish different abstract values
	 * during serialization.
	 * 
	 * @param key
	 *            An integer which bijectively defines a particular abstract
	 *            value
	 * @see #setKey
	 */
	protected AbstractValue(int key) {
		setKey(key);
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}
	
	// returns the abstract token corresponding to the key
	public AbstractValue getToken(int key) {
		throw new RuntimeException("get_token not implemented");
	}

	/**
	 * 
	 * @return The set of possible abstract values.
	 */
	public Set<AbstractValue> getTokens() {
		throw new RuntimeException("get_tokens not implemented");
	}

	/**
	 * 
	 * @return The number of possible abstract values.
	 */
	public int getTokensNumber() {
		throw new RuntimeException("get_num_tokens not implemented");
	}
	
	/**
	 * @return true, if this abstraction is a single value from the domain;
	 * false, if this abstraction represents a set of values from the domain.
	 */
	public boolean isComposite() {
		return getTokensNumber() > 1;
	}
	
	/**
	 * Computes abs_v2 + abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of sum.
	 * @see	#abstractMap(double)
	 * @see	#_plus(AbstractValue)
	 */
	public static AbstractValue _add(double v1, AbstractValue abs_v1, double v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._plus(abs_v1);
			else
				result = abs_v2._plus(v1);
		} else if (abs_v1 != null)
			result = abs_v1._plus(v2);
		return result;
	}

	/**
	 * Computes abs_v2 + abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of sum.
	 * @see	#abstractMap(float)
	 * @see	#_plus(AbstractValue)
	 */
	public static AbstractValue _add(float v1, AbstractValue abs_v1, float v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._plus(abs_v1);
			else
				result = abs_v2._plus(v1);
		} else if (abs_v1 != null)
			result = abs_v1._plus(v2);
		return result;
	}

	/**
	 * Computes abs_v2 + abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of sum.
	 * @see	#abstractMap(int)
	 * @see	#_plus(AbstractValue)
	 */
	public static AbstractValue _add(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._plus(abs_v1);
			else
				result = abs_v2._plus(v1);
		} else if (abs_v1 != null)
			result = abs_v1._plus(v2);
		return result;
	}

	/**
	 * Computes abs_v2 + abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of sum.
	 * @see	#abstractMap(long)
	 * @see	#_plus(AbstractValue)
	 */
	public static AbstractValue _add(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._plus(abs_v1);
			else
				result = abs_v2._plus(v1);
		} else if (abs_v1 != null)
			result = abs_v1._plus(v2);
		return result;
	}

	/**
	 * Computes abs_v2 & abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of bitwise conjunction.
	 * @see	#abstractMap(int)
	 * @see	#_bitwise_and(AbstractValue)
	 */
	public static AbstractValue _and(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._bitwise_and(abs_v1);
			else
				result = abs_v2._bitwise_and(v1);
		} else if (abs_v1 != null)
			result = abs_v1._bitwise_and(v2);
		return result;
	}

	/**
	 * Computes abs_v2 & abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of bitwise conjunction.
	 * @see	#abstractMap(long)
	 * @see	#_bitwise_and(AbstractValue)
	 */
	public static AbstractValue _and(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._bitwise_and(abs_v1);
			else
				result = abs_v2._bitwise_and(v1);
		} else if (abs_v1 != null)
			result = abs_v1._bitwise_and(v2);
		return result;
	}

	/**
	 * Compares two long integers, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of comparison.
	 * @see	#abstractMap(long)
	 * @see	#_cmp(AbstractValue)
	 */	
	public static AbstractValue _cmp(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._cmp(abs_v1);
			else
				result = abs_v2._cmp(v1);
		} else if (abs_v1 != null)
			result = abs_v1._cmp_reverse(v2);
		return result;
	}

	/**
	 * Compares two doubles, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of comparison.
	 * @see	#abstractMap(double)
	 * @see	#_cmp(AbstractValue)
	 */		
	public static AbstractValue _cmpg(double v1, AbstractValue abs_v1, double v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._cmpg(abs_v1);
			else
				result = abs_v2._cmpg(v1);
		} else if (abs_v1 != null)
			result = abs_v1._cmpg_reverse(v2);
		return result;
	}

	/**
	 * Compares two floats, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of comparison.
	 * @see	#abstractMap(float)
	 * @see	#_cmp(AbstractValue)
	 */		
	public static AbstractValue _cmpg(float v1, AbstractValue abs_v1, float v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._cmpg(abs_v1);
			else
				result = abs_v2._cmpg(v1);
		} else if (abs_v1 != null)
			result = abs_v1._cmpg_reverse(v2);
		return result;
	}

	/**
	 * Compares two doubles, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of comparison.
	 * @see	#abstractMap(double)
	 * @see	#_cmp(AbstractValue)
	 */			
	public static AbstractValue _cmpl(double v1, AbstractValue abs_v1, double v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._cmpl(abs_v1);
			else
				result = abs_v2._cmpl(v1);
		} else if (abs_v1 != null)
			result = abs_v1._cmpl_reverse(v2);
		return result;
	}

	/**
	 * Compares two floats, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of comparison.
	 * @see	#abstractMap(float)
	 * @see	#_cmp(AbstractValue)
	 */			
	public static AbstractValue _cmpl(float v1, AbstractValue abs_v1, float v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._cmpl(abs_v1);
			else
				result = abs_v2._cmpl(v1);
		} else if (abs_v1 != null)
			result = abs_v1._cmpl_reverse(v2);
		return result;
	}
	
	/**
	 * Computes abs_v2 / abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of division.
	 * @see	#abstractMap(double)
	 * @see	#_div(AbstractValue)
	 */
	public static AbstractValue _div(double v1, AbstractValue abs_v1, double v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._div(abs_v1);
			else
				result = abs_v2._div(v1);
		} else if (abs_v1 != null)
			result = abs_v1._div_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2 / abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of division.
	 * @see	#abstractMap(float)
	 * @see	#_div(AbstractValue)
	 */	
	public static AbstractValue _div(float v1, AbstractValue abs_v1, float v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._div(abs_v1);
			else
				result = abs_v2._div(v1);
		} else if (abs_v1 != null)
			result = abs_v1._div_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2 / abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of division.
	 * @see	#abstractMap(int)
	 * @see	#_div(AbstractValue)
	 */		
	public static AbstractValue _div(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._div(abs_v1);
			else
				result = abs_v2._div(v1);
		} else if (abs_v1 != null)
			result = abs_v1._div_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2 / abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of division.
	 * @see	#abstractMap(long)
	 * @see	#_div(AbstractValue)
	 */		
	public static AbstractValue _div(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._div(abs_v1);
			else
				result = abs_v2._div(v1);
		} else if (abs_v1 != null)
			result = abs_v1._div_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2 == abs_v1, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract boolean value of comparison.
	 * @see	#abstractMap(int)
	 * @see	#_eq(AbstractValue)
	 */			
	public static AbstractBoolean _eq(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractBoolean result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._eq(abs_v1);
			else
				result = abs_v2._eq(v1);
		} else if (abs_v1 != null)
			result = abs_v1._eq(v2);
		return result;
	}

	/**
	 * Computes abs_v2 >= abs_v1, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract boolean value of comparison.
	 * @see	#abstractMap(int)
	 * @see	#_ge(AbstractValue)
	 */				
	public static AbstractBoolean _ge(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractBoolean result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._ge(abs_v1);
			else
				result = abs_v2._ge(v1);
		} else if (abs_v1 != null)
			result = abs_v1._ge_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2 > abs_v1, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract boolean value of comparison.
	 * @see	#abstractMap(int)
	 * @see	#_gt(AbstractValue)
	 */				
	public static AbstractBoolean _gt(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractBoolean result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._gt(abs_v1);
			else
				result = abs_v2._gt(v1);
		} else if (abs_v1 != null)
			result = abs_v1._gt_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2 <= abs_v1, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract boolean value of comparison.
	 * @see	#abstractMap(int)
	 * @see	#_le(AbstractValue)
	 */				
	public static AbstractBoolean _le(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractBoolean result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._le(abs_v1);
			else
				result = abs_v2._le(v1);
		} else if (abs_v1 != null)
			result = abs_v1._le_reverse(v2);
		return result;
	}
	
	/**
	 * Computes abs_v2 < abs_v1, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract boolean value of comparison.
	 * @see	#abstractMap(int)
	 * @see	#_lt(AbstractValue)
	 */			
	public static AbstractBoolean _lt(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractBoolean result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._lt(abs_v1);
			else
				result = abs_v2._lt(v1);
		} else if (abs_v1 != null)
			result = abs_v1._lt_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2 * abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of multiplication.
	 * @see	#abstractMap(double)
	 * @see	#_mul(AbstractValue)
	 */				
	public static AbstractValue _mul(double v1, AbstractValue abs_v1, double v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._mul(abs_v1);
			else
				result = abs_v2._mul(v1);
		} else if (abs_v1 != null)
			result = abs_v1._mul(v2);
		return result;
	}

	/**
	 * Computes abs_v2 * abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of multiplication.
	 * @see	#abstractMap(float)
	 * @see	#_mul(AbstractValue)
	 */			
	public static AbstractValue _mul(float v1, AbstractValue abs_v1, float v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._mul(abs_v1);
			else
				result = abs_v2._mul(v1);
		} else if (abs_v1 != null)
			result = abs_v1._mul(v2);
		return result;
	}

	/**
	 * Computes abs_v2 * abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of multiplication.
	 * @see	#abstractMap(int)
	 * @see	#_mul(AbstractValue)
	 */			
	public static AbstractValue _mul(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._mul(abs_v1);
			else
				result = abs_v2._mul(v1);
		} else if (abs_v1 != null)
			result = abs_v1._mul(v2);
		return result;
	}

	/**
	 * Computes abs_v2 * abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of multiplication.
	 * @see	#abstractMap(long)
	 * @see	#_mul(AbstractValue)
	 */			
	public static AbstractValue _mul(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._mul(abs_v1);
			else
				result = abs_v2._mul(v1);
		} else if (abs_v1 != null)
			result = abs_v1._mul(v2);
		return result;
	}

	/**
	 * Computes abs_v2 != abs_v1, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract boolean value of comparison.
	 * @see	#abstractMap(int)
	 * @see	#_lt(AbstractValue)
	 */			
	public static AbstractBoolean _ne(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractBoolean result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._ne(abs_v1);
			else
				result = abs_v2._ne(v1);
		} else if (abs_v1 != null)
			result = abs_v1._ne(v2);
		return result;
	}

	/**
	 * 
	 * @param abs_v	An abstract value
	 * @return A negation of abs_v
	 * @see	#_neg()
	 */			
	public static AbstractValue _neg(AbstractValue abs_v) {
		if (abs_v != null)
			return abs_v._neg();
		else
			return null;
	}

	/**
	 * Computes abs_v2 | abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of bitwise disjunction.
	 * @see	#abstractMap(int)
	 * @see	#_bitwise_or(AbstractValue)
	 */			
	public static AbstractValue _or(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._bitwise_or(abs_v1);
			else
				result = abs_v2._bitwise_or(v1);
		} else if (abs_v1 != null)
			result = abs_v1._bitwise_or(v2);
		return result;
	}

	/**
	 * Computes abs_v2 | abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of bitwise disjunction.
	 * @see	#abstractMap(long)
	 * @see	#_bitwise_or(AbstractValue)
	 */				
	public static AbstractValue _or(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._bitwise_or(abs_v1);
			else
				result = abs_v2._bitwise_or(v1);
		} else if (abs_v1 != null)
			result = abs_v1._bitwise_or(v2);
		return result;
	}

	/**
	 * Computes abs_v2 % abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of remainder.
	 * @see	#abstractMap(double)
	 * @see	#_rem(AbstractValue)
	 */				
	public static AbstractValue _rem(double v1, AbstractValue abs_v1, double v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._rem(abs_v1);
			else
				result = abs_v2._rem(v1);
		} else if (abs_v1 != null)
			result = abs_v1._rem_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2 % abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of remainder.
	 * @see	#abstractMap(float)
	 * @see	#_rem(AbstractValue)
	 */					
	public static AbstractValue _rem(float v1, AbstractValue abs_v1, float v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._rem(abs_v1);
			else
				result = abs_v2._rem(v1);
		} else if (abs_v1 != null)
			result = abs_v1._rem_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2 % abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of remainder.
	 * @see	#abstractMap(int)
	 * @see	#_rem(AbstractValue)
	 */					
	public static AbstractValue _rem(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._rem(abs_v1);
			else
				result = abs_v2._rem(v1);
		} else if (abs_v1 != null)
			result = abs_v1._rem_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2 % abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of remainder.
	 * @see	#abstractMap(long)
	 * @see	#_rem(AbstractValue)
	 */					
	public static AbstractValue _rem(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._rem(abs_v1);
			else
				result = abs_v2._rem(v1);
		} else if (abs_v1 != null)
			result = abs_v1._rem_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2 << abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of shifting.
	 * @see	#abstractMap(int)
	 * @see	#_shift_left(AbstractValue)
	 */					
	public static AbstractValue _shl(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._shift_left(abs_v1);
			else
				result = abs_v2._shift_left(v1);
		} else if (abs_v1 != null)
			result = abs_v1._shift_left_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2 << abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of shifting.
	 * @see	#abstractMap(long)
	 * @see	#_shift_left(AbstractValue)
	 */					
	public static AbstractValue _shl(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._shift_left(abs_v1);
			else
				result = abs_v2._shift_left(v1);
		} else if (abs_v1 != null)
			result = abs_v1._shift_left_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2 >> abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of shifting.
	 * @see	#abstractMap(int)
	 * @see	#_shift_left(AbstractValue)
	 */					
	public static AbstractValue _shr(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._shift_right(abs_v1);
			else
				result = abs_v2._shift_right(v1);
		} else if (abs_v1 != null)
			result = abs_v1._shift_right_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2 >> abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of shifting.
	 * @see	#abstractMap(long)
	 * @see	#_shift_left(AbstractValue)
	 */					
	public static AbstractValue _shr(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._shift_right(abs_v1);
			else
				result = abs_v2._shift_right(v1);
		} else if (abs_v1 != null)
			result = abs_v1._shift_right_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2-abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of subtraction.
	 * @see	#abstractMap(double)
	 * @see	#_sub(AbstractValue)
	 */					
	public static AbstractValue _sub(double v1, AbstractValue abs_v1, double v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._minus(abs_v1);
			else
				result = abs_v2._minus(v1);
		} else if (abs_v1 != null)
			result = abs_v1._minus_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2-abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of subtraction.
	 * @see	#abstractMap(float)
	 * @see	#_sub(AbstractValue)
	 */				
	public static AbstractValue _sub(float v1, AbstractValue abs_v1, float v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._minus(abs_v1);
			else
				result = abs_v2._minus(v1);
		} else if (abs_v1 != null)
			result = abs_v1._minus_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2-abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of subtraction.
	 * @see	#abstractMap(int)
	 * @see	#_sub(AbstractValue)
	 */				
	public static AbstractValue _sub(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._minus(abs_v1);
			else
				result = abs_v2._minus(v1);
		} else if (abs_v1 != null)
			result = abs_v1._minus_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2-abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of subtraction.
	 * @see	#abstractMap(long)
	 * @see	#_sub(AbstractValue)
	 */				
	public static AbstractValue _sub(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._minus(abs_v1);
			else
				result = abs_v2._minus(v1);
		} else if (abs_v1 != null)
			result = abs_v1._minus_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2 >>> abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of shifting.
	 * @see	#abstractMap(int)
	 * @see	#_shift_left(AbstractValue)
	 */					
	public static AbstractValue _ushr(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._unsigned_shift_right(abs_v1);
			else
				result = abs_v2._unsigned_shift_right(v1);
		} else if (abs_v1 != null)
			result = abs_v1._unsigned_shift_right_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2 >>> abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of shifting.
	 * @see	#abstractMap(long)
	 * @see	#_shift_left(AbstractValue)
	 */					
	public static AbstractValue _ushr(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._unsigned_shift_right(abs_v1);
			else
				result = abs_v2._unsigned_shift_right(v1);
		} else if (abs_v1 != null)
			result = abs_v1._unsigned_shift_right_reverse(v2);
		return result;
	}

	/**
	 * Computes abs_v2 ^ abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of bitwise exclusive disjunction.
	 * @see	#abstractMap(int)
	 * @see	#_bitwise_xor(AbstractValue)
	 */				
	public static AbstractValue _xor(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._bitwise_xor(abs_v1);
			else
				result = abs_v2._bitwise_xor(v1);
		} else if (abs_v1 != null)
			result = abs_v1._bitwise_xor(v2);
		return result;
	}

	/**
	 * Computes abs_v2 ^ abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of bitwise exclusive disjunction.
	 * @see	#abstractMap(long)
	 * @see	#_bitwise_xor(AbstractValue)
	 */					
	public static AbstractValue _xor(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._bitwise_xor(abs_v1);
			else
				result = abs_v2._bitwise_xor(v1);
		} else if (abs_v1 != null)
			result = abs_v1._bitwise_xor(v2);
		return result;
	}

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         AbstractValue; Signs.NEG if this AbstractValue is numerically less
	 *         than the operand; and Signs.POS if this AbstractValue is
	 *         numerically greater than the operand.
	 */
	public AbstractValue _cmp(AbstractValue right) {
		// TODO: move to particular AbstractValues
		boolean n = false, z = false, p = false;
		if (this._gt(right) != AbstractBoolean.FALSE)
			p = true;
		if (this._lt(right) != AbstractBoolean.FALSE)
			n = true;
		if (this._gt(right) != AbstractBoolean.TRUE
				&& this._lt(right) != AbstractBoolean.TRUE)
			z = true;
		return SignsAbstraction.getInstance().create(n, z, p);
	}

	public AbstractValue _cmp(long right) {
		return this._cmp(abs.abstractMap(right));
	}

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         AbstractValue; Signs.NEG if this AbstractValue is numerically less
	 *         than the operand; and Signs.POS if this AbstractValue is
	 *         numerically greater than the operand.
	 */	
	public AbstractValue _cmpg(AbstractValue right) {
		// TODO: move to particular AbstractValues
		boolean n = false, z = false, p = false;
		if (this._gt(right) != AbstractBoolean.FALSE)
			p = true;
		if (this._lt(right) != AbstractBoolean.FALSE)
			n = true;
		if (this._gt(right) != AbstractBoolean.TRUE
				&& this._lt(right) != AbstractBoolean.TRUE)
			z = true;
		return SignsAbstraction.getInstance().create(n, z, p);
	}

	public AbstractValue _cmpg(float right) {
		return this._cmpg(abs.abstractMap(right));
	}

	public AbstractValue _cmpg(double right) {
		return this._cmpg(abs.abstractMap(right));
	}

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         AbstractValue; Signs.NEG if this AbstractValue is numerically less
	 *         than the operand; and Signs.POS if this AbstractValue is
	 *         numerically greater than the operand.
	 */	
	public AbstractValue _cmpl(AbstractValue right) {
		// TODO: move to particular AbstractValues
		boolean n = false, z = false, p = false;
		if (this._gt(right) != AbstractBoolean.FALSE)
			p = true;
		if (this._lt(right) != AbstractBoolean.FALSE)
			n = true;
		if (this._gt(right) != AbstractBoolean.TRUE
				&& this._lt(right) != AbstractBoolean.TRUE)
			z = true;
		return SignsAbstraction.getInstance().create(n, z, p);
	}

	public AbstractValue _cmpl(float right) {
		return this._cmpl(abs.abstractMap(right));
	}

	public AbstractValue _cmpl(double right) {
		return this._cmpl(abs.abstractMap(right));
	}

	public AbstractValue _bitwise_and(AbstractValue right) {
		throw new RuntimeException("bitwise and not implemented");
	}

	public AbstractValue _bitwise_and(int right) {
		throw new RuntimeException("bitwise and not implemented");
	}

	public AbstractValue _bitwise_and(long right) {
		throw new RuntimeException("bitwise and not implemented");
	}

	public AbstractValue _bitwise_or(AbstractValue right) {
		throw new RuntimeException("bitwise or not implemented");
	}

	public AbstractValue _bitwise_or(int right) {
		throw new RuntimeException("bitwise or not implemented");
	}

	public AbstractValue _bitwise_or(long right) {
		throw new RuntimeException("bitwise or not implemented");
	}

	public AbstractValue _bitwise_xor(AbstractValue right) {
		throw new RuntimeException("bitwise xor not implemented");
	}

	public AbstractValue _bitwise_xor(int right) {
		throw new RuntimeException("bitwise xor not implemented");
	}

	public AbstractValue _bitwise_xor(long right) {
		throw new RuntimeException("bitwise xor not implemented");
	}

	protected AbstractValue _cmp_reverse(long right) {
		throw new RuntimeException("cmp not implemented");
	}

	protected AbstractValue _cmpg_reverse(double right) {
		throw new RuntimeException("cmpg not implemented");
	}

	protected AbstractValue _cmpg_reverse(float right) {
		throw new RuntimeException("cmpg not implemented");
	}

	protected AbstractValue _cmpl_reverse(double right) {
		throw new RuntimeException("cmpl not implemented");
	}

	protected AbstractValue _cmpl_reverse(float right) {
		throw new RuntimeException("cmpl not implemented");
	}

	public AbstractValue _div(AbstractValue right) {
		throw new RuntimeException("div not implemented");
	}

	public AbstractValue _div(double right) {
		throw new RuntimeException("div not implemented");
	}

	public AbstractValue _div(float right) {
		throw new RuntimeException("div not implemented");
	}

	public AbstractValue _div(int right) {
		throw new RuntimeException("div not implemented");
	}

	public AbstractValue _div(long right) {
		throw new RuntimeException("div not implemented");
	}

	protected AbstractValue _div_reverse(double right) {
		throw new RuntimeException("div not implemented");
	}

	protected AbstractValue _div_reverse(float right) {
		throw new RuntimeException("div not implemented");
	}

	protected AbstractValue _div_reverse(int right) {
		throw new RuntimeException("div not implemented");
	}

	protected AbstractValue _div_reverse(long right) {
		throw new RuntimeException("div not implemented");
	}

	public AbstractBoolean _eq(AbstractValue right) {
		throw new RuntimeException("ge not implemented");
	}

	public AbstractBoolean _eq(int right) {
		throw new RuntimeException("ge not implemented");
	}

	public AbstractBoolean _ge(AbstractValue right) {
		throw new RuntimeException("ge not implemented");
	}

	public AbstractBoolean _ge(int right) {
		throw new RuntimeException("ge not implemented");
	}

	protected AbstractBoolean _ge_reverse(int right) {
		throw new RuntimeException("ge not implemented");
	}

	public AbstractBoolean _gt(AbstractValue right) {
		throw new RuntimeException("gt not implemented");
	}

	public AbstractBoolean _gt(int right) {
		throw new RuntimeException("gt not implemented");
	}

	protected AbstractBoolean _gt_reverse(int right) {
		throw new RuntimeException("gt not implemented");
	}

	public AbstractBoolean _le(AbstractValue right) {
		throw new RuntimeException("le not implemented");
	}

	public AbstractBoolean _le(int right) {
		throw new RuntimeException("le not implemented");
	}

	protected AbstractBoolean _le_reverse(int right) {
		throw new RuntimeException("le not implemented");
	}

	public AbstractBoolean _lt(AbstractValue right) {
		throw new RuntimeException("lt not implemented");
	}

	public AbstractBoolean _lt(int right) {
		throw new RuntimeException("lt not implemented");
	}

	protected AbstractBoolean _lt_reverse(int right) {
		throw new RuntimeException("lt not implemented");
	}

	public AbstractValue _minus(AbstractValue right) {
		throw new RuntimeException("minus not implemented");
	}

	public AbstractValue _minus(double right) {
		throw new RuntimeException("minus not implemented");
	}

	public AbstractValue _minus(float right) {
		throw new RuntimeException("minus not implemented");
	}

	public AbstractValue _minus(int right) {
		throw new RuntimeException("minus not implemented");
	}

	public AbstractValue _minus(long right) {
		throw new RuntimeException("minus not implemented");
	}

	protected AbstractValue _minus_reverse(double right) {
		throw new RuntimeException("minus not implemented");
	}

	protected AbstractValue _minus_reverse(float right) {
		throw new RuntimeException("minus not implemented");
	}

	protected AbstractValue _minus_reverse(int right) {
		throw new RuntimeException("minus not implemented");
	}

	protected AbstractValue _minus_reverse(long right) {
		throw new RuntimeException("minus not implemented");
	}

	public AbstractValue _mul(AbstractValue right) {
		throw new RuntimeException("mul not implemented");
	}

	public AbstractValue _mul(double right) {
		throw new RuntimeException("mul not implemented");
	}

	public AbstractValue _mul(float right) {
		throw new RuntimeException("mul not implemented");
	}

	public AbstractValue _mul(int right) {
		throw new RuntimeException("mul not implemented");
	}

	public AbstractValue _mul(long right) {
		throw new RuntimeException("mul not implemented");
	}

	public AbstractBoolean _ne(AbstractValue right) {
		throw new RuntimeException("ge not implemented");
	}

	public AbstractBoolean _ne(int right) {
		throw new RuntimeException("ge not implemented");
	}

	public AbstractValue _neg() {
		throw new RuntimeException("negation not implemented");
	}

	public AbstractValue _plus(AbstractValue right) {
		throw new RuntimeException("plus not implemented");
	}

	public AbstractValue _plus(double right) {
		throw new RuntimeException("plus not implemented");
	}

	public AbstractValue _plus(float right) {
		throw new RuntimeException("plus not implemented");
	}

	/**
	 * Computes the sum of this AbstractValue and abstract_map(right). It is a
	 * good idea to consider special cases when right is -1 or +1, because this
	 * can make increments and decrements more precise.
	 * 
	 * @return the result of adding operand to this AbstractValue
	 */
	public AbstractValue _plus(int right) {
		throw new RuntimeException("plus not implemented");
	}

	public AbstractValue _plus(long right) {
		throw new RuntimeException("plus not implemented");
	}

	public AbstractValue _rem(AbstractValue right) {
		throw new RuntimeException("rem not implemented");
	}

	public AbstractValue _rem(double right) {
		throw new RuntimeException("rem not implemented");
	}

	public AbstractValue _rem(float right) {
		throw new RuntimeException("rem not implemented");
	}

	public AbstractValue _rem(int right) {
		throw new RuntimeException("rem not implemented");
	}

	public AbstractValue _rem(long right) {
		throw new RuntimeException("rem not implemented");
	}

	protected AbstractValue _rem_reverse(double right) {
		throw new RuntimeException("rem not implemented");
	}

	protected AbstractValue _rem_reverse(float right) {
		throw new RuntimeException("rem not implemented");
	}

	protected AbstractValue _rem_reverse(int right) {
		throw new RuntimeException("rem not implemented");
	}

	protected AbstractValue _rem_reverse(long right) {
		throw new RuntimeException("rem not implemented");
	}

	public AbstractValue _shift_left(AbstractValue right) {
		throw new RuntimeException("shift left not implemented");
	}

	public AbstractValue _shift_left(int right) {
		throw new RuntimeException("shift left not implemented");
	}

	public AbstractValue _shift_left(long right) {
		throw new RuntimeException("shift left not implemented");
	}

	protected AbstractValue _shift_left_reverse(int right) {
		throw new RuntimeException("shift left not implemented");
	}

	protected AbstractValue _shift_left_reverse(long right) {
		throw new RuntimeException("shift left not implemented");
	}

	public AbstractValue _shift_right(AbstractValue right) {
		throw new RuntimeException("shift right not implemented");
	}

	public AbstractValue _shift_right(int right) {
		throw new RuntimeException("shift right not implemented");
	}

	public AbstractValue _shift_right(long right) {
		throw new RuntimeException("shift right not implemented");
	}

	protected AbstractValue _shift_right_reverse(int right) {
		throw new RuntimeException("shift right not implemented");
	}

	protected AbstractValue _shift_right_reverse(long right) {
		throw new RuntimeException("shift right not implemented");
	}

	public AbstractValue _unsigned_shift_right(AbstractValue right) {
		throw new RuntimeException("shift right not implemented");
	}

	public AbstractValue _unsigned_shift_right(int right) {
		throw new RuntimeException("shift right not implemented");
	}

	public AbstractValue _unsigned_shift_right(long right) {
		throw new RuntimeException("shift right not implemented");
	}

	protected AbstractValue _unsigned_shift_right_reverse(int right) {
		throw new RuntimeException("unsigned shift right not implemented");
	}

	protected AbstractValue _unsigned_shift_right_reverse(long right) {
		throw new RuntimeException("shift right not implemented");
	}

	public boolean equals(AbstractValue abs) {
		if (!getClass().getName().equals(abs.getClass().getName()))
			throw new RuntimeException("### Error: Comparing different AbstractValues");
		return (getClass().getName().equals(abs.getClass().getName()))
				&& (this.getKey() == abs.getKey());
	}
}
