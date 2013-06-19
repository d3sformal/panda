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
 * The domain of this abstraction consists of two values: EVEN and ODD.
 * Numeric values are mapped to one of them depending on their remainder by modulo 2.
 * 
 * When the result of an abstract operation cannot be defined unambiguously
 * (e.g. ODD % ODD can be both ODD and EVEN), the special "composite token" TOP
 * returned.
 */
public class EvennessAbstraction extends Abstraction {
	
	private static EvennessAbstraction instance;
	
	public static EvennessAbstraction getInstance() {
		if (instance == null)
		{
			instance = new EvennessAbstraction();
		}
		
		return instance;
	}
	
	private EvennessAbstraction() {
	}

	public static EvennessValue EVEN = new EvennessValue(0);
	public static EvennessValue ODD = new EvennessValue(1);
	public static EvennessValue TOP = new EvennessValue(2);

    private static final int DOMAIN_SIZE = 2;

    /**
	 * 
	 * @return The number of abstract values in the domain.
	 */
    @Override
	public int getDomainSize() {
		return DOMAIN_SIZE;
	}

	public EvennessValue create(boolean isEven, boolean isOdd) {
		if (isEven)
			if (isOdd)
				return TOP;
			else
				return EVEN;
		else
			if (isOdd)
				return ODD;
			else
				throw new RuntimeException("Abstraction is out of range");
	}	
	
	@Override
	public EvennessValue abstractMap(int v) {
		if (v % 2 == 0)
			return EVEN;
		else
			return ODD;
	}

	@Override
	public EvennessValue abstractMap(long v) {
		if (v % 2 == 0)
			return EVEN;
		else
			return ODD;
	}
		
}
