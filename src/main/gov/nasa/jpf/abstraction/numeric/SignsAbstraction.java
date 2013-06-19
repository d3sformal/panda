// Copyright (C) 2012 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.

// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.

// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.

package gov.nasa.jpf.abstraction.numeric;

/**
 * The domain of this abstraction consists of three values: NEG, ZERO, and POS.
 * Numeric values are mapped to one of them depending on their signs.
 * 
 * When the result of an abstract operation cannot be defined unambiguously
 * (e.g. POS + NEG can be NEG, ZERO or POS), special "composite tokens" which
 * represent a set of abstract values (like NON_NEG is {ZERO, POS}) are
 * returned.
 * 
 * Remember, that this abstraction does not handle such floating-point values as
 * NaN and INF.
 */
public class SignsAbstraction extends Abstraction {
	
	private static SignsAbstraction instance;
	
	public static SignsAbstraction getInstance() {
		if (instance == null)
		{
			instance = new SignsAbstraction();
		}
		
		return instance;
	}

	private SignsAbstraction() {
	}
	
	// basic tokens: NEG, ZERO, POS
	public static SignsValue NEG = new SignsValue(0);
	public static SignsValue ZERO = new SignsValue(1);
	public static SignsValue POS = new SignsValue(2);
	// composite tokens: NON_NEG, NON_ZERO, NON_POS, TOP
	public static SignsValue NON_NEG = new SignsValue(3);
	public static SignsValue NON_ZERO = new SignsValue(4);
	public static SignsValue NON_POS = new SignsValue(5);
	public static SignsValue TOP = new SignsValue(6);

	private static final int DOMAIN_SIZE = 3;

	/**
	 * @param isNeg  indicates whether the new abstraction can be negative
	 * @param isZero indicates whether the new abstraction can be zero
	 * @param isPos  indicates whether the new abstraction can be positive 
	 * @return the new abstraction with specified values
	 */		
	public SignsValue create(boolean isNeg, boolean isZero,
			boolean isPos) {
		if (isNeg)
			if (isZero)
				if (isPos)
					return TOP;
				else
					return NON_POS;
			else if (isPos)
				return NON_ZERO;
			else
				return NEG;
		else if (isZero)
			if (isPos)
				return NON_NEG;
			else
				return ZERO;
		else if (isPos)
			return POS;
		else
			throw new RuntimeException("Abstraction is out of range");
	}

	@Override
	public SignsValue abstractMap(int v) {
		if (v > 0)
			return POS;
		if (v == 0)
			return ZERO;
		// if (v < 0)
		return NEG;
	}

	@Override
	public SignsValue abstractMap(long v) {
		if (v > 0)
			return POS;
		if (v == 0)
			return ZERO;
		// if (v < 0)
		return NEG;
	}

	@Override
	public SignsValue abstractMap(float v) {
		if (v > 0)
			return POS;
		if (v == 0)
			return ZERO;
		if (v < 0)
			return NEG;
		// NaN or INF
		return null;
	}

	@Override
	public SignsValue abstractMap(double v) {
		if (v > 0)
			return POS;
		if (v == 0)
			return ZERO;
		if (v < 0)
			return NEG;
		// NaN or INF
		return null;
	}

	/**
	 * 
	 * @return The number of abstract values in the domain.
	 */
    @Override
	public int getDomainSize() {
		return DOMAIN_SIZE;
	}

}
