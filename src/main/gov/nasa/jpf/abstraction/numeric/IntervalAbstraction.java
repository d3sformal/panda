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
public class IntervalAbstraction extends Abstraction {
	public static IntervalAbstraction defaultAbstraction = new IntervalAbstraction(Double.NaN, Double.NaN);

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
	
	public IntervalAbstraction(double min, double max) {
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
	
	@Override
	public AbstractValue _plus(AbstractValue left, int right) {
		IntervalValue left_value = (IntervalValue) left;
		
		if (right == 1) {
			boolean gr = left_value.can_be_INSIDE() || left_value.can_be_GREATER();
			boolean in = (left_value.can_be_INSIDE() && ((IntervalAbstraction)left.abs).MIN != ((IntervalAbstraction)left.abs).MAX) || left_value.can_be_LESS();
			boolean le = left_value.can_be_LESS();
			return ((IntervalAbstraction)left.abs).create(le, in, gr);
		} else if (right == -1) {
			boolean le = left_value.can_be_INSIDE() || left_value.can_be_LESS();
			boolean in = (left_value.can_be_INSIDE() && ((IntervalAbstraction)left.abs).MIN != ((IntervalAbstraction)left.abs).MAX)
					|| left_value.can_be_GREATER();
			boolean gr = left_value.can_be_GREATER();
			return ((IntervalAbstraction)left.abs).create(le, in, gr);
		} else
			return _plus(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _plus(AbstractValue left, long right) {
		return _plus(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _plus(AbstractValue left, float right) {
		return _plus(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _plus(AbstractValue left, double right) {
		return _plus(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _plus(AbstractValue left, AbstractValue right) {
		if (right instanceof IntervalValue) {
			IntervalValue left_value = (IntervalValue) left;
			IntervalValue right_value = (IntervalValue) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (left_value.can_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, ((IntervalAbstraction)left.abs).MIN);
			}
			if (left_value.can_be_INSIDE()) {
				left1 = Math.min(left1, ((IntervalAbstraction)left.abs).MIN);
				right1 = Math.max(right1, ((IntervalAbstraction)left.abs).MAX);
			}
			if (left_value.can_be_GREATER()) {
				left1 = Math.min(left1, ((IntervalAbstraction)left.abs).MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.can_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, ((IntervalAbstraction)left.abs).MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, ((IntervalAbstraction)left.abs).MIN);
				right2 = Math.max(right2, ((IntervalAbstraction)left.abs).MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, ((IntervalAbstraction)left.abs).MAX);
				right2 = Math.max(right2, Double.POSITIVE_INFINITY);
			}
			double res_left = left1 + left2, res_right = right1 + right2;
			return ((IntervalAbstraction)left.abs).create(res_left < ((IntervalAbstraction)left.abs).MIN, res_right >= ((IntervalAbstraction)left.abs).MIN
					&& res_left <= ((IntervalAbstraction)left.abs).MAX, res_right > ((IntervalAbstraction)left.abs).MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _minus(AbstractValue left, AbstractValue right) {
		if (right instanceof IntervalValue) {
			IntervalValue left_value = (IntervalValue) left;
			IntervalValue right_value = (IntervalValue) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (left_value.can_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, ((IntervalAbstraction)left.abs).MIN);
			}
			if (left_value.can_be_INSIDE()) {
				left1 = Math.min(left1, ((IntervalAbstraction)left.abs).MIN);
				right1 = Math.max(right1, ((IntervalAbstraction)left.abs).MAX);
			}
			if (left_value.can_be_GREATER()) {
				left1 = Math.min(left1, ((IntervalAbstraction)left.abs).MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.can_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, ((IntervalAbstraction)left.abs).MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, ((IntervalAbstraction)left.abs).MIN);
				right2 = Math.max(right2, ((IntervalAbstraction)left.abs).MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, ((IntervalAbstraction)left.abs).MAX);
				right2 = Math.max(right2, Double.POSITIVE_INFINITY);
			}
			double res_left = left1 - right2, res_right = right1 - left2;
			return ((IntervalAbstraction)left.abs).create(res_left < ((IntervalAbstraction)left.abs).MIN, res_right >= ((IntervalAbstraction)left.abs).MIN
					&& res_left <= ((IntervalAbstraction)left.abs).MAX, res_right > ((IntervalAbstraction)left.abs).MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _minus(AbstractValue left, int right) {
		return _minus(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _minus(AbstractValue left, long right) {
		return _minus(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _minus(AbstractValue left, float right) {
		return _minus(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _minus(AbstractValue left, double right) {
		return _minus(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _mul(AbstractValue left, int right) {
		return _mul(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _mul(AbstractValue left, long right) {
		return _mul(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _mul(AbstractValue left, float right) {
		return _mul(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _mul(AbstractValue left, double right) {
		return _mul(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _mul(AbstractValue left, AbstractValue right) {
		if (right instanceof IntervalValue) {
			IntervalValue left_value = (IntervalValue) left;
			IntervalValue right_value = (IntervalValue) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (left_value.can_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, ((IntervalAbstraction)left.abs).MIN);
			}
			if (left_value.can_be_INSIDE()) {
				left1 = Math.min(left1, ((IntervalAbstraction)left.abs).MIN);
				right1 = Math.max(right1, ((IntervalAbstraction)left.abs).MAX);
			}
			if (left_value.can_be_GREATER()) {
				left1 = Math.min(left1, ((IntervalAbstraction)left.abs).MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.can_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, ((IntervalAbstraction)left.abs).MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, ((IntervalAbstraction)left.abs).MIN);
				right2 = Math.max(right2, ((IntervalAbstraction)left.abs).MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, ((IntervalAbstraction)left.abs).MAX);
				right2 = Math.max(right2, Double.POSITIVE_INFINITY);
			}
			double res_left, res_right;
			if ((left1 <= 0 && 0 <= right1) || (left2 <= 0 && 0 <= right2)) {
				res_left = ___min(left1 * left2, left1 * right2,
						right1 * left2, right1 * right2, 0);
				res_right = ____max(left1 * left2, left1 * right2, right1
						* left2, right1 * right2, 0);
			} else {
				res_left = ___min(left1 * left2, left1 * right2,
						right1 * left2, right1 * right2);
				res_right = ____max(left1 * left2, left1 * right2, right1
						* left2, right1 * right2);
			}
			return ((IntervalAbstraction)left.abs).create(res_left < ((IntervalAbstraction)left.abs).MIN, res_right >= ((IntervalAbstraction)left.abs).MIN
					&& res_left <= ((IntervalAbstraction)left.abs).MAX, res_right > ((IntervalAbstraction)left.abs).MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _div(AbstractValue left, int right) {
		return _div(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _div(AbstractValue left, long right) {
		return _div(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _div(AbstractValue left, float right) {
		return _div(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _div(AbstractValue left, double right) {
		return _div(left, left.abs.abstractMap(right));
	}

	private double ___min(double... args) {
		double res = args[0];
		for (int i = 1; i < args.length; ++i)
			if (args[i] < res)
				res = args[i];
		return res;
	}

	private double ____max(double... args) {
		double res = args[0];
		for (int i = 1; i < args.length; ++i)
			if (args[i] > res)
				res = args[i];
		return res;
	}

	@Override
	public AbstractValue _div(AbstractValue left, AbstractValue right) {
		if (right instanceof IntervalValue) {
			IntervalValue left_value = (IntervalValue) left;
			IntervalValue right_value = (IntervalValue) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (left_value.can_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, ((IntervalAbstraction)left.abs).MIN);
			}
			if (left_value.can_be_INSIDE()) {
				left1 = Math.min(left1, ((IntervalAbstraction)left.abs).MIN);
				right1 = Math.max(right1, ((IntervalAbstraction)left.abs).MAX);
			}
			if (left_value.can_be_GREATER()) {
				left1 = Math.min(left1, ((IntervalAbstraction)left.abs).MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.can_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, ((IntervalAbstraction)left.abs).MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, ((IntervalAbstraction)left.abs).MIN);
				right2 = Math.max(right2, ((IntervalAbstraction)left.abs).MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, ((IntervalAbstraction)left.abs).MAX);
				right2 = Math.max(right2, Double.POSITIVE_INFINITY);
			}
			if (left2 <= 0 && 0 <= right2)
				System.out.println("### WARNING: Division by ZERO may happen");
			// TODO: Handle division by zero
			double res_left, res_right;
			if ((left1 <= 0 && 0 <= right1) || (left2 < 0 && 0 < right2)) {
				res_left = ___min(left1 / left2, left1 / right2,
						right1 / left2, right1 / right2, 0);
				res_right = ____max(left1 / left2, left1 / right2, right1
						/ left2, right1 / right2, 0);
			} else {
				res_left = ___min(left1 / left2, left1 / right2,
						right1 / left2, right1 / right2);
				res_right = ____max(left1 / left2, left1 / right2, right1
						/ left2, right1 / right2);
			}
			return ((IntervalAbstraction)left.abs).create(res_left < ((IntervalAbstraction)left.abs).MIN, res_right >= ((IntervalAbstraction)left.abs).MIN
					&& res_left <= ((IntervalAbstraction)left.abs).MAX, res_right > ((IntervalAbstraction)left.abs).MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _rem(AbstractValue left, int right) {
		return _rem(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _rem(AbstractValue left, long right) {
		return _rem(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _rem(AbstractValue left, float right) {
		return _rem(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _rem(AbstractValue left, double right) {
		return _rem(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _rem(AbstractValue left, AbstractValue right) {
		if (right instanceof IntervalValue) {
			IntervalValue left_value = (IntervalValue) left;
			IntervalValue right_value = (IntervalValue) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (left_value.can_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, ((IntervalAbstraction)left.abs).MIN);
			}
			if (left_value.can_be_INSIDE()) {
				left1 = Math.min(left1, ((IntervalAbstraction)left.abs).MIN);
				right1 = Math.max(right1, ((IntervalAbstraction)left.abs).MAX);
			}
			if (left_value.can_be_GREATER()) {
				left1 = Math.min(left1, ((IntervalAbstraction)left.abs).MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.can_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, ((IntervalAbstraction)left.abs).MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, ((IntervalAbstraction)left.abs).MIN);
				right2 = Math.max(right2, ((IntervalAbstraction)left.abs).MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, ((IntervalAbstraction)left.abs).MAX);
				right2 = Math.max(right2, Double.POSITIVE_INFINITY);
			}
			if (left2 <= 0 && 0 <= right2)
				; // TODO: Handle division by zero
			double res_left = 0;
			double res_right = ____max(Math.abs(left1), Math.abs(right1),
					Math.abs(left2), Math.abs(right2));

			return ((IntervalAbstraction)left.abs).create(res_left < ((IntervalAbstraction)left.abs).MIN, res_right >= ((IntervalAbstraction)left.abs).MIN
					&& res_left <= ((IntervalAbstraction)left.abs).MAX, res_right > ((IntervalAbstraction)left.abs).MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _bitwise_and(AbstractValue left, int right) {
		return _bitwise_and(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _bitwise_and(AbstractValue left, long right) {
		return _bitwise_and(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _bitwise_and(AbstractValue left, AbstractValue right) {
		if (right instanceof IntervalValue) {
			return ((IntervalAbstraction)left.abs).create(true, true, true);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _bitwise_or(AbstractValue left, int right) {
		return _bitwise_or(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _bitwise_or(AbstractValue left, long right) {
		return _bitwise_or(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _bitwise_or(AbstractValue left, AbstractValue right) {
		if (right instanceof IntervalValue) {
			return ((IntervalAbstraction)left.abs).create(true, true, true);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _bitwise_xor(AbstractValue left, int right) {
		return _bitwise_xor(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _bitwise_xor(AbstractValue left, long right) {
		return _bitwise_xor(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _bitwise_xor(AbstractValue left, AbstractValue right) {
		if (right instanceof IntervalValue) {
			return ((IntervalAbstraction)left.abs).create(true, true, true);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _shift_left(AbstractValue left, int right) {
		return _shift_left(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _shift_left(AbstractValue left, long right) {
		return _shift_left(left, left.abs.abstractMap(right));
	}

	// Note that x << y considers only the least five bits of y
	@Override
	public AbstractValue _shift_left(AbstractValue left, AbstractValue right) {
		if (right instanceof IntervalValue) {
			return ((IntervalAbstraction)left.abs).create(true, true, true);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _shift_right(AbstractValue left, int right) {
		return _shift_right(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _shift_right(AbstractValue left, long right) {
		return _shift_right(left, left.abs.abstractMap(right));
	}

	// Note that x >> y considers only the least five bits of y, sign of x is
	// preserved
	@Override
	public AbstractValue _shift_right(AbstractValue left, AbstractValue right) {
		if (right instanceof IntervalValue) {
			return ((IntervalAbstraction)left.abs).create(true, true, true);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _unsigned_shift_right(AbstractValue left, int right) {
		return _unsigned_shift_right(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _unsigned_shift_right(AbstractValue left, long right) {
		return _unsigned_shift_right(left, left.abs.abstractMap(right));
	}

	// Note that x >>> y considers only the least five bits of y, sign of x is
	// not preserved
	@Override
	public AbstractValue _unsigned_shift_right(AbstractValue left, AbstractValue right) {
		if (right instanceof IntervalValue) {
			return ((IntervalAbstraction)left.abs).create(true, true, true);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _neg_impl(AbstractValue abs) {
		IntervalValue value = (IntervalValue) abs;

		double left = Double.POSITIVE_INFINITY, right = Double.NEGATIVE_INFINITY;
		if (value.can_be_LESS()) {
			left = Math.min(left, Double.NEGATIVE_INFINITY);
			right = Math.max(right, ((IntervalAbstraction)abs.abs).MIN);
		}
		if (value.can_be_INSIDE()) {
			left = Math.min(left, ((IntervalAbstraction)abs.abs).MIN);
			right = Math.max(right, ((IntervalAbstraction)abs.abs).MAX);
		}
		if (value.can_be_GREATER()) {
			left = Math.min(left, ((IntervalAbstraction)abs.abs).MAX);
			right = Math.max(right, Double.POSITIVE_INFINITY);
		}
		double res_right = -left;
		double res_left = -right;
		return ((IntervalAbstraction)abs.abs).create(res_left < ((IntervalAbstraction)abs.abs).MIN, res_right >= ((IntervalAbstraction)abs.abs).MIN
				&& res_left <= ((IntervalAbstraction)abs.abs).MAX, res_right > ((IntervalAbstraction)abs.abs).MAX);
	}

	@Override
	public AbstractBoolean _ge(AbstractValue left, AbstractValue right) {
		if (right instanceof IntervalValue) {
			IntervalValue left_value = (IntervalValue) left;
			IntervalValue right_value = (IntervalValue) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (left_value.can_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, ((IntervalAbstraction)left.abs).MIN);
			}
			if (left_value.can_be_INSIDE()) {
				left1 = Math.min(left1, ((IntervalAbstraction)left.abs).MIN);
				right1 = Math.max(right1, ((IntervalAbstraction)left.abs).MAX);
			}
			if (left_value.can_be_GREATER()) {
				left1 = Math.min(left1, ((IntervalAbstraction)left.abs).MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.can_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, ((IntervalAbstraction)left.abs).MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, ((IntervalAbstraction)left.abs).MIN);
				right2 = Math.max(right2, ((IntervalAbstraction)left.abs).MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, ((IntervalAbstraction)left.abs).MAX);
				right2 = Math.max(right2, Double.POSITIVE_INFINITY);
			}
			boolean t = right1 >= left2; // this.MAX >= right.MIN
			boolean f = left1 < right2;
			if (f & t)
				return AbstractBoolean.TOP;
			else
				return (f) ? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _ge(AbstractValue left, int right) {
		return _ge(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractBoolean _gt(AbstractValue left, AbstractValue right) {
		if (right instanceof IntervalValue) {
			IntervalValue left_value = (IntervalValue) left;
			IntervalValue right_value = (IntervalValue) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (left_value.can_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, ((IntervalAbstraction)left.abs).MIN);
			}
			if (left_value.can_be_INSIDE()) {
				left1 = Math.min(left1, ((IntervalAbstraction)left.abs).MIN);
				right1 = Math.max(right1, ((IntervalAbstraction)left.abs).MAX);
			}
			if (left_value.can_be_GREATER()) {
				left1 = Math.min(left1, ((IntervalAbstraction)left.abs).MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.can_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, ((IntervalAbstraction)left.abs).MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, ((IntervalAbstraction)left.abs).MIN);
				right2 = Math.max(right2, ((IntervalAbstraction)left.abs).MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, ((IntervalAbstraction)left.abs).MAX);
				right2 = Math.max(right2, Double.POSITIVE_INFINITY);
			}
			boolean t = right1 > left2; // this.MAX >= right.MIN
			boolean f = left1 <= right2;
			if (f & t)
				return AbstractBoolean.TOP;
			else
				return (f) ? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _gt(AbstractValue left, int right) {
		return _gt(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractBoolean _le(AbstractValue left, AbstractValue right) {
		if (right instanceof IntervalValue) {
			IntervalValue left_value = (IntervalValue) left;
			IntervalValue right_value = (IntervalValue) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (left_value.can_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, ((IntervalAbstraction)left.abs).MIN);
			}
			if (left_value.can_be_INSIDE()) {
				left1 = Math.min(left1, ((IntervalAbstraction)left.abs).MIN);
				right1 = Math.max(right1, ((IntervalAbstraction)left.abs).MAX);
			}
			if (left_value.can_be_GREATER()) {
				left1 = Math.min(left1, ((IntervalAbstraction)left.abs).MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.can_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, ((IntervalAbstraction)left.abs).MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, ((IntervalAbstraction)left.abs).MIN);
				right2 = Math.max(right2, ((IntervalAbstraction)left.abs).MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, ((IntervalAbstraction)left.abs).MAX);
				right2 = Math.max(right2, Double.POSITIVE_INFINITY);
			}
			boolean t = right2 >= left1; // this.MAX >= right.MIN
			boolean f = left2 < right1;
			if (f & t)
				return AbstractBoolean.TOP;
			else
				return (f) ? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _le(AbstractValue left, int right) {
		return _le(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractBoolean _lt(AbstractValue left, AbstractValue right) {
		if (right instanceof IntervalValue) {
			IntervalValue left_value = (IntervalValue) left;
			IntervalValue right_value = (IntervalValue) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (left_value.can_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, ((IntervalAbstraction)left.abs).MIN);
			}
			if (left_value.can_be_INSIDE()) {
				left1 = Math.min(left1, ((IntervalAbstraction)left.abs).MIN);
				right1 = Math.max(right1, ((IntervalAbstraction)left.abs).MAX);
			}
			if (left_value.can_be_GREATER()) {
				left1 = Math.min(left1, ((IntervalAbstraction)left.abs).MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.can_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, ((IntervalAbstraction)left.abs).MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, ((IntervalAbstraction)left.abs).MIN);
				right2 = Math.max(right2, ((IntervalAbstraction)left.abs).MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, ((IntervalAbstraction)left.abs).MAX);
				right2 = Math.max(right2, Double.POSITIVE_INFINITY);
			}
			boolean t = right2 > left1; // this.MAX >= right.MIN
			boolean f = left2 <= right1;
			if (f & t)
				return AbstractBoolean.TOP;
			else
				return (f) ? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _lt(AbstractValue left, int right) {
		return _lt(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractBoolean _eq(AbstractValue left, AbstractValue right) {
		if (right instanceof IntervalValue) {
			IntervalValue left_value = (IntervalValue) left;
			IntervalValue right_value = (IntervalValue) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (left_value.can_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, ((IntervalAbstraction)left.abs).MIN);
			}
			if (left_value.can_be_INSIDE()) {
				left1 = Math.min(left1, ((IntervalAbstraction)left.abs).MIN);
				right1 = Math.max(right1, ((IntervalAbstraction)left.abs).MAX);
			}
			if (left_value.can_be_GREATER()) {
				left1 = Math.min(left1, ((IntervalAbstraction)left.abs).MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.can_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, ((IntervalAbstraction)left.abs).MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, ((IntervalAbstraction)left.abs).MIN);
				right2 = Math.max(right2, ((IntervalAbstraction)left.abs).MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, ((IntervalAbstraction)left.abs).MAX);
				right2 = Math.max(right2, Double.POSITIVE_INFINITY);
			}
			// values can be equal, iff equality is possible (bounding segments
			// can intersect)
			boolean t = (left1 >= left2 && left1 <= right2)
					|| (right1 >= left2 && right1 <= right2)
					|| (left2 >= left1 && left2 <= right1)
					|| (right2 >= left1 && right2 <= right1);
			// values can be inequal, iff their domains are different
			boolean f = !(left1 == left2 && right1 == right2);
			if (f & t)
				return AbstractBoolean.TOP;
			else
				return (f) ? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _eq(AbstractValue left, int right) {
		return _eq(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractBoolean _ne(AbstractValue left, AbstractValue right) {
		return _eq(left, right).not();
	}

	@Override
	public AbstractBoolean _ne(AbstractValue left, int right) {
		return _ne(left, left.abs.abstractMap(right));
	}

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         AbstractValue; Signs.NEG if this AbstractValue is numerically less
	 *         than the operand; and Signs.POS if this AbstractValue is
	 *         numerically greater than the operand.
	 */
	@Override
	public AbstractValue _cmp(AbstractValue left, AbstractValue right) {
		boolean n = false, z = false, p = false;
		if (_gt(left, right) != AbstractBoolean.FALSE)
			p = true;
		if (_lt(left, right) != AbstractBoolean.FALSE)
			n = true;
		if (_gt(left, right) != AbstractBoolean.TRUE
				&& _lt(left, right) != AbstractBoolean.TRUE)
			z = true;
		return SignsAbstraction.getInstance().create(n, z, p);
	}

	@Override
	public AbstractValue _cmp(AbstractValue left, long right) {
		return _cmp(left, left.abs.abstractMap(right));
	}

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         AbstractValue; Signs.NEG if this AbstractValue is numerically less
	 *         than the operand; and Signs.POS if this AbstractValue is
	 *         numerically greater than the operand.
	 */	
	@Override
	public AbstractValue _cmpg(AbstractValue left, AbstractValue right) {
		boolean n = false, z = false, p = false;
		if (_gt(left, right) != AbstractBoolean.FALSE)
			p = true;
		if (_lt(left, right) != AbstractBoolean.FALSE)
			n = true;
		if (_gt(left, right) != AbstractBoolean.TRUE
				&& _lt(left, right) != AbstractBoolean.TRUE)
			z = true;
		return SignsAbstraction.getInstance().create(n, z, p);
	}

	@Override
	public AbstractValue _cmpg(AbstractValue left, float right) {
		return _cmpg(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _cmpg(AbstractValue left, double right) {
		return _cmpg(left, left.abs.abstractMap(right));
	}

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         AbstractValue; Signs.NEG if this AbstractValue is numerically less
	 *         than the operand; and Signs.POS if this AbstractValue is
	 *         numerically greater than the operand.
	 */	
	@Override
	public AbstractValue _cmpl(AbstractValue left, AbstractValue right) {
		boolean n = false, z = false, p = false;
		if (_gt(left, right) != AbstractBoolean.FALSE)
			p = true;
		if (_lt(left, right) != AbstractBoolean.FALSE)
			n = true;
		if (_gt(left, right) != AbstractBoolean.TRUE
				&& _lt(left, right) != AbstractBoolean.TRUE)
			z = true;
		return SignsAbstraction.getInstance().create(n, z, p);
	}

	@Override
	public AbstractValue _cmpl(AbstractValue left, float right) {
		return _cmpl(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _cmpl(AbstractValue left, double right) {
		return _cmpl(left, left.abs.abstractMap(right));
	}	
	
	@Override
	protected AbstractValue _div_reverse(AbstractValue left, int right) {
		return _div(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _div_reverse(AbstractValue left, long right) {
		return _div(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _div_reverse(AbstractValue left, float right) {
		return _div(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _div_reverse(AbstractValue left, double right) {
		return _div(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _cmp_reverse(AbstractValue left, long right) {
		return _cmp(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _cmpl_reverse(AbstractValue left, float right) {
		return _cmpl(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _cmpl_reverse(AbstractValue left, double right) {
		return _cmpl(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _cmpg_reverse(AbstractValue left, float right) {
		return _cmpg(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _cmpg_reverse(AbstractValue left, double right) {
		return _cmpg(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _rem_reverse(AbstractValue left, int right) {
		return _rem(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _rem_reverse(AbstractValue left, long right) {
		return _rem(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _rem_reverse(AbstractValue left, float right) {
		return _rem(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _rem_reverse(AbstractValue left, double right) {
		return _rem(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _shift_left_reverse(AbstractValue left, int right) {
		return _shift_left(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _shift_left_reverse(AbstractValue left, long right) {
		return _shift_left(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _shift_right_reverse(AbstractValue left, int right) {
		return _shift_right(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _shift_right_reverse(AbstractValue left, long right) {
		return _shift_right(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _unsigned_shift_right_reverse(AbstractValue left, int right) {
		return _unsigned_shift_right(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _unsigned_shift_right_reverse(AbstractValue left, long right) {
		return _unsigned_shift_right(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractBoolean _lt_reverse(AbstractValue left, int right) {
		return _lt(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractBoolean _le_reverse(AbstractValue left, int right) {
		return _le(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractBoolean _ge_reverse(AbstractValue left, int right) {
		return _ge(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractBoolean _gt_reverse(AbstractValue left, int right) {
		return _gt(left.abs.abstractMap(right), left);
	}
	
}
