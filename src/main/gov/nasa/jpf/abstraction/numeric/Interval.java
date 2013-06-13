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

/**
 * The abstract domain for given two integer or floating-point values MIN and
 * MAX is the set { LESS, INSIDE, GREATER }, whose elements express the fact
 * that a value is less than MIN, between MIN and MAX, or greater than MAX,
 * respectively. This abstraction can be used both for integer values and
 * floating-point values.
 * 
 * When the result of an abstract operation cannot be defined unambiguously
 * (e.g. LESS + INSIDE can be LESS, INSIDE or GREATER), special
 * "composite tokens" which represent a set of abstract values (like NOT_LESS is
 * {INSIDE, GREATER}) are returned.
 * 
 * Remember, that this abstraction does not handle such floating-point values as
 * NaN and INF.
 */
public class Interval extends Abstraction {
	public static Interval defaultAbstraction = new Interval(Double.NaN, Double.NaN);

	public static enum AbstractIntervalValueType {
		LESS(0),
		INSIDE(1),
		GREATER(2),
		NOT_LESS(3),
		OUTSIDE(4),
		NOT_GREATER(5),
		TOP(6);
		
		public int key;

		private AbstractIntervalValueType(int key) {
			this.key = key;
		}
	}
	
	public double MIN;
	public double MAX;
	
	private static final int DOMAIN_SIZE = 3;
	
	public Interval(double min, double max) {
		MIN = min;
		MAX = max;
	}

	/**
	 * 
	 * @return The number of abstract values in the domain.
	 */
	public int getDomainSize() {
		return DOMAIN_SIZE;
	}	
		
	public IntervalValue create(int key) {
		IntervalValue in = new IntervalValue(key);
		
		in.abs = this;

		return in;
	}
	
	public IntervalValue createTop()
	{
		return create(AbstractIntervalValueType.TOP.key);
	}
	
	public IntervalValue createNotGreater()
	{
		return create(AbstractIntervalValueType.NOT_GREATER.key);
	}
	
	public IntervalValue createInside()
	{
		return create(AbstractIntervalValueType.INSIDE.key);
	}
	
	public IntervalValue createNotLess()
	{
		return create(AbstractIntervalValueType.NOT_LESS.key);
	}
	
	public IntervalValue createGreater()
	{
		return create(AbstractIntervalValueType.GREATER.key);
	}
	
	public IntervalValue createOutside()
	{
		return create(AbstractIntervalValueType.OUTSIDE.key);
	}

	public IntervalValue createLess()
	{
		return create(AbstractIntervalValueType.LESS.key);
	}
	
	public IntervalValue create(boolean isLess, boolean isInside,
			boolean isGreater) {
		if (isLess)
			if (isInside)
				if (isGreater)
					return createTop();
				else
					return createNotGreater();
			else if (isGreater)
				return createGreater();
			else
				return createLess();
		else if (isInside)
			if (isGreater)
				return createNotLess();
			else
				return createInside();
		else if (isGreater)
			return createGreater();
		else
			throw new RuntimeException("Abstraction is out of range");
	}

	@Override
	public IntervalValue abstractMap(int v) {
		if (v > MAX)
			return createGreater();
		if (v < MIN)
			return createLess();
		return createInside();
	}

	@Override
	public IntervalValue abstractMap(long v) {
		if (v > MAX)
			return createGreater();
		if (v < MIN)
			return createLess();
		return createInside();
	}

	@Override
	public IntervalValue abstractMap(float v) {
		if (v > MAX)
			return createGreater();
		if (v < MIN)
			return createLess();
		if (MIN <= v && v <= MAX)
			return createInside();
		// NaN
		return null;
	}

	@Override
	public IntervalValue abstractMap(double v) {
		if (v > MAX)
			return createGreater();
		if (v < MIN)
			return createLess();
		if (MIN <= v && v <= MAX)
			return createInside();
		// NaN
		return null;
	}
}
