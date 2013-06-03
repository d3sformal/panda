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

import java.util.HashSet;
import java.util.Set;

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
	// basic tokens: LESS, INSIDE, GREATER
	public static Interval LESS = new Interval(0);
	public static Interval INSIDE = new Interval(1);
	public static Interval GREATER = new Interval(2);
	// composite tokens:  NOT_LESS, OUTSIDE, NOT_GREATER, TOP
	public static Interval NOT_LESS = new Interval(3);
	public static Interval OUTSIDE = new Interval(4);
	public static Interval NOT_GREATER = new Interval(5);
	public static Interval TOP = new Interval(6);

	private static double MIN;
	private static double MAX;
	
	private static final int DOMAIN_SIZE = 3;

	public boolean can_be_GREATER() {
		int key = this.getKey();
		return key == 2 || key == 3 || key == 4 || key == 6;
	}

	public boolean cab_be_LESS() {
		int key = this.getKey();
		return key == 0 || key == 4 || key == 5 || key == 6;
	}

	public boolean can_be_INSIDE() {
		int key = this.getKey();
		return key == 1 || key == 3 || key == 5 || key == 6;
	}
	
	@Override
	public Set<Abstraction> getTokens() {
		Set<Abstraction> tokens = new HashSet<Abstraction>();
		if (cab_be_LESS())
			tokens.add(LESS);
		if (can_be_INSIDE())
			tokens.add(INSIDE);
		if (can_be_GREATER())
			tokens.add(GREATER);
		return tokens;
	}

	// returns possible tokens (enumerated from 0) in order {NEG, ZERO, POS}
	@Override
	public Abstraction getToken(int key) {
		int num = getTokensNumber();
		if (key < 0 || key >= num)
			throw new RuntimeException("Wrong TOP token");
		if (cab_be_LESS())
			if (key == 0)
				return LESS;
			else if (can_be_INSIDE())
				return (key == 1) ? INSIDE : GREATER;
			else
				return GREATER;
		else if (can_be_INSIDE())
			return (key == 0) ? INSIDE : GREATER;
		else
			return GREATER;
	}

	@Override
	public int getTokensNumber() {
		int result = 0;
		if (can_be_GREATER())
			++result;
		if (cab_be_LESS())
			++result;
		if (can_be_INSIDE())
			++result;
		return result;
	}

	/**
	 * 
	 * @return The number of abstract values in the domain.
	 */
	public int getDomainSize() {
		return DOMAIN_SIZE;
	}	

	/**
	 * @return true, if this abstraction is a single value from the domain;
	 * false, if this abstraction represents a set of values from the domain.
	 */
	@Override
	public boolean isComposite() {
		return getKey() > 2;
	}	
	
	/** 
	 * Should be used only once in AbstractInstructionFactory
	 * @return the new abstraction for [MIN, MAX] interval 
	 */			
	public static Interval create(double MIN, double MAX) {
		// TODO: rewrite this
		Interval in = new Interval(0);
		in.MIN = MIN;
		in.MAX = MAX;
		return in;
	}

	private Interval(int key) {
		super(key);
		MIN = MAX = Double.NaN;
	}

	private Interval create(boolean isLess, boolean isInside,
			boolean isGreater) {
		if (isLess)
			if (isInside)
				if (isGreater)
					return TOP;
				else
					return NOT_GREATER;
			else if (isGreater)
				return OUTSIDE;
			else
				return LESS;
		else if (isInside)
			if (isGreater)
				return NOT_LESS;
			else
				return INSIDE;
		else if (isGreater)
			return GREATER;
		else
			throw new RuntimeException("Abstraction is out of range");
	}

	@Override
	public Interval abstractMap(int v) {
		if (v > MAX)
			return GREATER;
		if (v < MIN)
			return LESS;
		return INSIDE;
	}

	@Override
	public Interval abstractMap(long v) {
		if (v > MAX)
			return GREATER;
		if (v < MIN)
			return LESS;
		return INSIDE;
	}

	@Override
	public Interval abstractMap(float v) {
		if (v > MAX)
			return GREATER;
		if (v < MIN)
			return LESS;
		if (MIN <= v && v <= MAX)
			return INSIDE;
		// NaN
		return null;
	}

	@Override
	public Interval abstractMap(double v) {
		if (v > MAX)
			return GREATER;
		if (v < MIN)
			return LESS;
		if (MIN <= v && v <= MAX)
			return INSIDE;
		// NaN
		return null;
	}

	@Override
	public Abstraction _plus(int right) {
		if (right == 1) {
			boolean gr = can_be_INSIDE() || can_be_GREATER();
			boolean in = (can_be_INSIDE() && MIN != MAX) || cab_be_LESS();
			boolean le = cab_be_LESS();
			return create(le, in, gr);
		} else if (right == -1) {
			boolean le = can_be_INSIDE() || cab_be_LESS();
			boolean in = (can_be_INSIDE() && MIN != MAX)
					|| can_be_GREATER();
			boolean gr = can_be_GREATER();
			return create(le, in, gr);
		} else
			return _plus(abstractMap(right));
	}

	@Override
	public Abstraction _plus(long right) {
		return _plus(abstractMap(right));
	}

	@Override
	public Abstraction _plus(float right) {
		return _plus(abstractMap(right));
	}

	@Override
	public Abstraction _plus(double right) {
		return _plus(abstractMap(right));
	}

	@Override
	public Abstraction _plus(Abstraction right) {
		if (right instanceof Interval) {
			Interval right_value = (Interval) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (this.cab_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, MIN);
			}
			if (this.can_be_INSIDE()) {
				left1 = Math.min(left1, MIN);
				right1 = Math.max(right1, MAX);
			}
			if (this.can_be_GREATER()) {
				left1 = Math.min(left1, MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.cab_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, MIN);
				right2 = Math.max(right2, MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, MAX);
				right2 = Math.max(right2, Double.POSITIVE_INFINITY);
			}
			double res_left = left1 + left2, res_right = right1 + right2;
			return create(res_left < MIN, res_right >= MIN
					&& res_left <= MAX, res_right > MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _minus(Abstraction right) {
		if (right instanceof Interval) {
			Interval right_value = (Interval) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (this.cab_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, MIN);
			}
			if (this.can_be_INSIDE()) {
				left1 = Math.min(left1, MIN);
				right1 = Math.max(right1, MAX);
			}
			if (this.can_be_GREATER()) {
				left1 = Math.min(left1, MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.cab_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, MIN);
				right2 = Math.max(right2, MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, MAX);
				right2 = Math.max(right2, Double.POSITIVE_INFINITY);
			}
			double res_left = left1 - right2, res_right = right1 - left2;
			return create(res_left < MIN, res_right >= MIN
					&& res_left <= MAX, res_right > MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _minus(int right) {
		return _minus(abstractMap(right));
	}

	@Override
	public Abstraction _minus(long right) {
		return _minus(abstractMap(right));
	}

	@Override
	public Abstraction _minus(float right) {
		return _minus(abstractMap(right));
	}

	@Override
	public Abstraction _minus(double right) {
		return _minus(abstractMap(right));
	}

	@Override
	public Abstraction _mul(int right) {
		return _mul(abstractMap(right));
	}

	@Override
	public Abstraction _mul(long right) {
		return _mul(abstractMap(right));
	}

	@Override
	public Abstraction _mul(float right) {
		return _mul(abstractMap(right));
	}

	@Override
	public Abstraction _mul(double right) {
		return _mul(abstractMap(right));
	}

	@Override
	public Abstraction _mul(Abstraction right) {
		if (right instanceof Interval) {
			Interval right_value = (Interval) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (this.cab_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, MIN);
			}
			if (this.can_be_INSIDE()) {
				left1 = Math.min(left1, MIN);
				right1 = Math.max(right1, MAX);
			}
			if (this.can_be_GREATER()) {
				left1 = Math.min(left1, MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.cab_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, MIN);
				right2 = Math.max(right2, MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, MAX);
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
			return create(res_left < MIN, res_right >= MIN
					&& res_left <= MAX, res_right > MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _div(int right) {
		return _div(abstractMap(right));
	}

	@Override
	public Abstraction _div(long right) {
		return _div(abstractMap(right));
	}

	@Override
	public Abstraction _div(float right) {
		return _div(abstractMap(right));
	}

	@Override
	public Abstraction _div(double right) {
		return _div(abstractMap(right));
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
	public Abstraction _div(Abstraction right) {
		if (right instanceof Interval) {
			Interval right_value = (Interval) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (this.cab_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, MIN);
			}
			if (this.can_be_INSIDE()) {
				left1 = Math.min(left1, MIN);
				right1 = Math.max(right1, MAX);
			}
			if (this.can_be_GREATER()) {
				left1 = Math.min(left1, MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.cab_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, MIN);
				right2 = Math.max(right2, MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, MAX);
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
			return create(res_left < MIN, res_right >= MIN
					&& res_left <= MAX, res_right > MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _rem(int right) {
		return _rem(abstractMap(right));
	}

	@Override
	public Abstraction _rem(long right) {
		return _rem(abstractMap(right));
	}

	@Override
	public Abstraction _rem(float right) {
		return _rem(abstractMap(right));
	}

	@Override
	public Abstraction _rem(double right) {
		return _rem(abstractMap(right));
	}

	@Override
	public Abstraction _rem(Abstraction right) {
		if (right instanceof Interval) {
			Interval right_value = (Interval) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (this.cab_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, MIN);
			}
			if (this.can_be_INSIDE()) {
				left1 = Math.min(left1, MIN);
				right1 = Math.max(right1, MAX);
			}
			if (this.can_be_GREATER()) {
				left1 = Math.min(left1, MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.cab_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, MIN);
				right2 = Math.max(right2, MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, MAX);
				right2 = Math.max(right2, Double.POSITIVE_INFINITY);
			}
			if (left2 <= 0 && 0 <= right2)
				; // TODO: Handle division by zero
			double res_left = 0;
			double res_right = ____max(Math.abs(left1), Math.abs(right1),
					Math.abs(left2), Math.abs(right2));

			return create(res_left < MIN, res_right >= MIN
					&& res_left <= MAX, res_right > MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _bitwise_and(int right) {
		return _bitwise_and(abstractMap(right));
	}

	@Override
	public Abstraction _bitwise_and(long right) {
		return _bitwise_and(abstractMap(right));
	}

	@Override
	public Abstraction _bitwise_and(Abstraction right) {
		if (right instanceof Interval) {
			return create(true, true, true);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _bitwise_or(int right) {
		return _bitwise_or(abstractMap(right));
	}

	@Override
	public Abstraction _bitwise_or(long right) {
		return _bitwise_or(abstractMap(right));
	}

	@Override
	public Abstraction _bitwise_or(Abstraction right) {
		if (right instanceof Interval) {
			return create(true, true, true);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _bitwise_xor(int right) {
		return _bitwise_xor(abstractMap(right));
	}

	@Override
	public Abstraction _bitwise_xor(long right) {
		return _bitwise_xor(abstractMap(right));
	}

	@Override
	public Abstraction _bitwise_xor(Abstraction right) {
		if (right instanceof Interval) {
			return create(true, true, true);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _shift_left(int right) {
		return _shift_left(abstractMap(right));
	}

	@Override
	public Abstraction _shift_left(long right) {
		return _shift_left(abstractMap(right));
	}

	// Note that x << y considers only the least five bits of y
	@Override
	public Abstraction _shift_left(Abstraction right) {
		if (right instanceof Interval) {
			return create(true, true, true);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _shift_right(int right) {
		return _shift_right(abstractMap(right));
	}

	@Override
	public Abstraction _shift_right(long right) {
		return _shift_right(abstractMap(right));
	}

	// Note that x >> y considers only the least five bits of y, sign of x is
	// preserved
	@Override
	public Abstraction _shift_right(Abstraction right) {
		if (right instanceof Interval) {
			return create(true, true, true);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _unsigned_shift_right(int right) {
		return _unsigned_shift_right(abstractMap(right));
	}

	@Override
	public Abstraction _unsigned_shift_right(long right) {
		return _unsigned_shift_right(abstractMap(right));
	}

	// Note that x >>> y considers only the least five bits of y, sign of x is
	// not preserved
	@Override
	public Abstraction _unsigned_shift_right(Abstraction right) {
		if (right instanceof Interval) {
			return create(true, true, true);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _neg() {
		double left = Double.POSITIVE_INFINITY, right = Double.NEGATIVE_INFINITY;
		if (this.cab_be_LESS()) {
			left = Math.min(left, Double.NEGATIVE_INFINITY);
			right = Math.max(right, MIN);
		}
		if (this.can_be_INSIDE()) {
			left = Math.min(left, MIN);
			right = Math.max(right, MAX);
		}
		if (this.can_be_GREATER()) {
			left = Math.min(left, MAX);
			right = Math.max(right, Double.POSITIVE_INFINITY);
		}
		double res_right = -left;
		double res_left = -right;
		return create(res_left < MIN, res_right >= MIN
				&& res_left <= MAX, res_right > MAX);
	}

	@Override
	public AbstractBoolean _ge(Abstraction right) {
		if (right instanceof Interval) {
			Interval right_value = (Interval) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (this.cab_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, MIN);
			}
			if (this.can_be_INSIDE()) {
				left1 = Math.min(left1, MIN);
				right1 = Math.max(right1, MAX);
			}
			if (this.can_be_GREATER()) {
				left1 = Math.min(left1, MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.cab_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, MIN);
				right2 = Math.max(right2, MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, MAX);
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
	public AbstractBoolean _ge(int right) {
		return _ge(abstractMap(right));
	}

	@Override
	public AbstractBoolean _gt(Abstraction right) {
		if (right instanceof Interval) {
			Interval right_value = (Interval) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (this.cab_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, MIN);
			}
			if (this.can_be_INSIDE()) {
				left1 = Math.min(left1, MIN);
				right1 = Math.max(right1, MAX);
			}
			if (this.can_be_GREATER()) {
				left1 = Math.min(left1, MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.cab_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, MIN);
				right2 = Math.max(right2, MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, MAX);
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
	public AbstractBoolean _gt(int right) {
		return _gt(abstractMap(right));
	}

	@Override
	public AbstractBoolean _le(Abstraction right) {
		if (right instanceof Interval) {
			Interval right_value = (Interval) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (this.cab_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, MIN);
			}
			if (this.can_be_INSIDE()) {
				left1 = Math.min(left1, MIN);
				right1 = Math.max(right1, MAX);
			}
			if (this.can_be_GREATER()) {
				left1 = Math.min(left1, MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.cab_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, MIN);
				right2 = Math.max(right2, MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, MAX);
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
	public AbstractBoolean _le(int right) {
		return _le(abstractMap(right));
	}

	@Override
	public AbstractBoolean _lt(Abstraction right) {
		if (right instanceof Interval) {
			Interval right_value = (Interval) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (this.cab_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, MIN);
			}
			if (this.can_be_INSIDE()) {
				left1 = Math.min(left1, MIN);
				right1 = Math.max(right1, MAX);
			}
			if (this.can_be_GREATER()) {
				left1 = Math.min(left1, MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.cab_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, MIN);
				right2 = Math.max(right2, MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, MAX);
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
	public AbstractBoolean _lt(int right) {
		return _lt(abstractMap(right));
	}

	public AbstractBoolean _eq(Abstraction right) {
		if (right instanceof Interval) {
			Interval right_value = (Interval) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (this.cab_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, MIN);
			}
			if (this.can_be_INSIDE()) {
				left1 = Math.min(left1, MIN);
				right1 = Math.max(right1, MAX);
			}
			if (this.can_be_GREATER()) {
				left1 = Math.min(left1, MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.cab_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, MIN);
				right2 = Math.max(right2, MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, MAX);
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

	public AbstractBoolean _eq(int right) {
		return _eq(abstractMap(right));
	}

	public AbstractBoolean _ne(Abstraction right) {
		return _eq(right).not();
	}

	@Override
	public AbstractBoolean _ne(int right) {
		return _ne(abstractMap(right));
	}

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         Abstraction; Signs.NEG if this Abstraction is numerically less
	 *         than the operand; and Signs.POS if this Abstraction is
	 *         numerically greater than the operand.
	 */
	@Override
	public Abstraction _cmp(Abstraction right) {
		boolean n = false, z = false, p = false;
		if (this._gt(right) != AbstractBoolean.FALSE)
			p = true;
		if (this._lt(right) != AbstractBoolean.FALSE)
			n = true;
		if (this._gt(right) != AbstractBoolean.TRUE
				&& this._lt(right) != AbstractBoolean.TRUE)
			z = true;
		return Signs.create(n, z, p);
	}

	@Override
	public Abstraction _cmp(long right) {
		return this._cmp(abstractMap(right));
	}

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         Abstraction; Signs.NEG if this Abstraction is numerically less
	 *         than the operand; and Signs.POS if this Abstraction is
	 *         numerically greater than the operand.
	 */	
	@Override
	public Abstraction _cmpg(Abstraction right) {
		boolean n = false, z = false, p = false;
		if (this._gt(right) != AbstractBoolean.FALSE)
			p = true;
		if (this._lt(right) != AbstractBoolean.FALSE)
			n = true;
		if (this._gt(right) != AbstractBoolean.TRUE
				&& this._lt(right) != AbstractBoolean.TRUE)
			z = true;
		return Signs.create(n, z, p);
	}

	@Override
	public Abstraction _cmpg(float right) {
		return this._cmpg(abstractMap(right));
	}

	@Override
	public Abstraction _cmpg(double right) {
		return this._cmpg(abstractMap(right));
	}

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         Abstraction; Signs.NEG if this Abstraction is numerically less
	 *         than the operand; and Signs.POS if this Abstraction is
	 *         numerically greater than the operand.
	 */	
	@Override
	public Abstraction _cmpl(Abstraction right) {
		boolean n = false, z = false, p = false;
		if (this._gt(right) != AbstractBoolean.FALSE)
			p = true;
		if (this._lt(right) != AbstractBoolean.FALSE)
			n = true;
		if (this._gt(right) != AbstractBoolean.TRUE
				&& this._lt(right) != AbstractBoolean.TRUE)
			z = true;
		return Signs.create(n, z, p);
	}

	@Override
	public Abstraction _cmpl(float right) {
		return this._cmpl(abstractMap(right));
	}

	@Override
	public Abstraction _cmpl(double right) {
		return this._cmpl(abstractMap(right));
	}	
	
	@Override
	protected Abstraction _div_reverse(int right) {
		return abstractMap(right)._div(this);
	}

	@Override
	protected Abstraction _div_reverse(long right) {
		return abstractMap(right)._div(this);
	}

	@Override
	protected Abstraction _div_reverse(float right) {
		return abstractMap(right)._div(this);
	}

	@Override
	protected Abstraction _div_reverse(double right) {
		return abstractMap(right)._div(this);
	}

	@Override
	protected Abstraction _cmp_reverse(long right) {
		return abstractMap(right)._cmp(this);
	}

	@Override
	protected Abstraction _cmpl_reverse(float right) {
		return abstractMap(right)._cmpl(this);
	}

	@Override
	protected Abstraction _cmpl_reverse(double right) {
		return abstractMap(right)._cmpl(this);
	}

	@Override
	protected Abstraction _cmpg_reverse(float right) {
		return abstractMap(right)._cmpg(this);
	}

	@Override
	protected Abstraction _cmpg_reverse(double right) {
		return abstractMap(right)._cmpg(this);
	}

	@Override
	protected Abstraction _rem_reverse(int right) {
		return abstractMap(right)._rem(this);
	}

	@Override
	protected Abstraction _rem_reverse(long right) {
		return abstractMap(right)._rem(this);
	}

	@Override
	protected Abstraction _rem_reverse(float right) {
		return abstractMap(right)._rem(this);
	}

	@Override
	protected Abstraction _rem_reverse(double right) {
		return abstractMap(right)._rem(this);
	}

	@Override
	protected Abstraction _shift_left_reverse(int right) {
		return abstractMap(right)._shift_left(this);
	}

	@Override
	protected Abstraction _shift_left_reverse(long right) {
		return abstractMap(right)._shift_left(this);
	}

	@Override
	protected Abstraction _shift_right_reverse(int right) {
		return abstractMap(right)._shift_right(this);
	}

	@Override
	protected Abstraction _shift_right_reverse(long right) {
		return abstractMap(right)._shift_right(this);
	}

	@Override
	protected Abstraction _unsigned_shift_right_reverse(int right) {
		return abstractMap(right)._unsigned_shift_right(this);
	}

	@Override
	protected Abstraction _unsigned_shift_right_reverse(long right) {
		return abstractMap(right)._unsigned_shift_right(this);
	}

	@Override
	protected AbstractBoolean _lt_reverse(int right) {
		return abstractMap(right)._lt(this);
	}

	@Override
	protected AbstractBoolean _le_reverse(int right) {
		return abstractMap(right)._le(this);
	}

	@Override
	protected AbstractBoolean _ge_reverse(int right) {
		return abstractMap(right)._ge(this);
	}

	@Override
	protected AbstractBoolean _gt_reverse(int right) {
		return abstractMap(right)._gt(this);
	}

	public String toString() {
		if (this instanceof Interval) {
			if (this == LESS)
				return "(-INF, MIN)";
			if (this == INSIDE)
				return "[MIN, MAX]";
			if (this == GREATER)
				return "(MAX, +INF)";
			if (this.isComposite()) {
				String s = null;
				if (this.cab_be_LESS())
					s = "(-INF, MIN)";
				if (this.can_be_INSIDE())
					if (s.isEmpty())
						s = "[MIN, MAX]";
					else
						s += "or [MIN, MAX]";
				if (this.can_be_GREATER())
					if (s.isEmpty())
						s = "(MAX, +INF)";
					else
						s += "or (MAX, +INF)";
				return s;
			}
		} else
			throw new RuntimeException("## Error: unknown abstraction");
		return "";
	}
}
