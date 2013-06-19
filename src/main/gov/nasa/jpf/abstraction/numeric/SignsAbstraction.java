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
    
    @Override
	public AbstractValue _plus(AbstractValue left, int right) {
    	SignsValue left_value = (SignsValue) left;

		if (right == 1) {
			boolean n = left_value.can_be_NEG();
			boolean z = left_value.can_be_NEG();
			boolean p = left_value.can_be_ZERO() || left_value.can_be_POS();
			return SignsAbstraction.getInstance().create(n, z, p);
		} else if (right == -1) {
			boolean n = left_value.can_be_NEG() || left_value.can_be_ZERO();
			boolean z = left_value.can_be_POS();
			boolean p = left_value.can_be_POS();
			return SignsAbstraction.getInstance().create(n, z, p);
		} else
			return _plus(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _plus(AbstractValue left, long right) {
		SignsValue left_value = (SignsValue) left;

		if (right == 1) {
			boolean n = left_value.can_be_NEG();
			boolean z = left_value.can_be_NEG();
			boolean p = left_value.can_be_ZERO() || left_value.can_be_POS();
			return SignsAbstraction.getInstance().create(n, z, p);
		} else if (right == -1) {
			boolean n = left_value.can_be_NEG() || left_value.can_be_ZERO();
			boolean z = left_value.can_be_POS();
			boolean p = left_value.can_be_POS();
			return SignsAbstraction.getInstance().create(n, z, p);
		} else
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
		if (right instanceof SignsValue) {
			SignsValue left_value = (SignsValue) left;
			SignsValue right_value = (SignsValue) right;
			boolean p = false, n = false, z = false;
			if (left_value.can_be_NEG() && right_value.can_be_NEG())
				n = true;
			if (left_value.can_be_NEG() && right_value.can_be_ZERO())
				n = true;
			if (left_value.can_be_NEG() && right_value.can_be_POS())
				n = z = p = true;
			if (left_value.can_be_ZERO() && right_value.can_be_NEG())
				n = true;
			if (left_value.can_be_ZERO() && right_value.can_be_ZERO())
				z = true;
			if (left_value.can_be_ZERO() && right_value.can_be_POS())
				p = true;
			if (left_value.can_be_POS() && right_value.can_be_NEG())
				n = z = p = true;
			if (left_value.can_be_POS() && right_value.can_be_ZERO())
				p = true;
			if (left_value.can_be_POS() && right_value.can_be_POS())
				p = true;
			return SignsAbstraction.getInstance().create(n, z, p);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _minus(AbstractValue left, AbstractValue right) {
		if (right instanceof SignsValue) {
			SignsValue left_value = (SignsValue) left;
			SignsValue right_value = (SignsValue) right;
			boolean p = false, n = false, z = false;
			if (left_value.can_be_NEG() && right_value.can_be_NEG())
				n = z = p = true;
			if (left_value.can_be_NEG() && right_value.can_be_ZERO())
				n = true;
			if (left_value.can_be_NEG() && right_value.can_be_POS())
				n = true;
			if (left_value.can_be_ZERO() && right_value.can_be_NEG())
				p = true;
			if (left_value.can_be_ZERO() && right_value.can_be_ZERO())
				z = true;
			if (left_value.can_be_ZERO() && right_value.can_be_POS())
				n = true;
			if (left_value.can_be_POS() && right_value.can_be_NEG())
				p = true;
			if (left_value.can_be_POS() && right_value.can_be_ZERO())
				p = true;
			if (left_value.can_be_POS() && right_value.can_be_POS())
				n = z = p = true;
			return SignsAbstraction.getInstance().create(n, z, p);
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
		if (right instanceof SignsValue) {
			SignsValue left_value = (SignsValue) left;
			SignsValue right_value = (SignsValue) right;
			boolean p = false, n = false, z = false;
			if (left_value.can_be_NEG() && right_value.can_be_NEG())
				p = true;
			if (left_value.can_be_NEG() && right_value.can_be_ZERO())
				z = true;
			if (left_value.can_be_NEG() && right_value.can_be_POS())
				n = true;
			if (left_value.can_be_ZERO() && right_value.can_be_NEG())
				z = true;
			if (left_value.can_be_ZERO() && right_value.can_be_ZERO())
				z = true;
			if (left_value.can_be_ZERO() && right_value.can_be_POS())
				z = true;
			if (left_value.can_be_POS() && right_value.can_be_NEG())
				n = true;
			if (left_value.can_be_POS() && right_value.can_be_ZERO())
				z = true;
			if (left_value.can_be_POS() && right_value.can_be_POS())
				p = true;
			return SignsAbstraction.getInstance().create(n, z, p);
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

	@Override
	public AbstractValue _div(AbstractValue left, AbstractValue right) {
		if (right instanceof SignsValue) {
			SignsValue left_value = (SignsValue) left;
			SignsValue right_value = (SignsValue) right;
			if (right_value.can_be_ZERO())
				throw new ArithmeticException(
						"Division by zero (in abstract IDIV)");
			boolean p = false, n = false, z = false;
			if (left_value.can_be_NEG() && right_value.can_be_NEG())
				p = true;
			if (left_value.can_be_NEG() && right_value.can_be_POS())
				n = true;
			if (left_value.can_be_ZERO() && right_value.can_be_NEG())
				z = true;
			if (left_value.can_be_ZERO() && right_value.can_be_POS())
				z = true;
			if (left_value.can_be_POS() && right_value.can_be_NEG())
				n = true;
			if (left_value.can_be_POS() && right_value.can_be_POS())
				p = true;
			return SignsAbstraction.getInstance().create(n, z, p);
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
		if (right instanceof SignsValue) {
			SignsValue left_value = (SignsValue) left;
			SignsValue right_value = (SignsValue) right;
			if (right_value.can_be_ZERO())
				throw new ArithmeticException(
						"Division by zero (in abstract IDIV)");
			boolean p = false, n = false, z = false;
			if (left_value.can_be_NEG() && right_value.can_be_NEG())
				z = p = true;
			if (left_value.can_be_NEG() && right_value.can_be_POS())
				z = p = true;
			if (left_value.can_be_ZERO() && right_value.can_be_NEG())
				z = true;
			if (left_value.can_be_ZERO() && right_value.can_be_POS())
				z = true;
			if (left_value.can_be_POS() && right_value.can_be_NEG())
				z = p = true;
			if (left_value.can_be_POS() && right_value.can_be_POS())
				z = p = true;
			return SignsAbstraction.getInstance().create(n, z, p);
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
		if (right instanceof SignsValue) {
			SignsValue left_value = (SignsValue) left;
			SignsValue right_value = (SignsValue) right;
			boolean p = false, n = false, z = false;
			if (left_value.can_be_NEG() && right_value.can_be_NEG())
				n = true;
			if (left_value.can_be_NEG() && right_value.can_be_ZERO())
				z = true;
			if (left_value.can_be_NEG() && right_value.can_be_POS())
				z = p = true;
			if (left_value.can_be_ZERO() && right_value.can_be_NEG())
				z = true;
			if (left_value.can_be_ZERO() && right_value.can_be_ZERO())
				z = true;
			if (left_value.can_be_ZERO() && right_value.can_be_POS())
				z = true;
			if (left_value.can_be_POS() && right_value.can_be_NEG())
				z = p = true;
			if (left_value.can_be_POS() && right_value.can_be_ZERO())
				z = true;
			if (left_value.can_be_POS() && right_value.can_be_POS())
				z = p = true;
			return SignsAbstraction.getInstance().create(n, z, p);
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
		if (right instanceof SignsValue) {
			SignsValue left_value = (SignsValue) left;
			SignsValue right_value = (SignsValue) right;
			boolean p = false, n = false, z = false;
			if (left_value.can_be_NEG() && right_value.can_be_NEG())
				n = true;
			if (left_value.can_be_NEG() && right_value.can_be_ZERO())
				n = true;
			if (left_value.can_be_NEG() && right_value.can_be_POS())
				n = true;
			if (left_value.can_be_ZERO() && right_value.can_be_NEG())
				n = true;
			if (left_value.can_be_ZERO() && right_value.can_be_ZERO())
				z = true;
			if (left_value.can_be_ZERO() && right_value.can_be_POS())
				p = true;
			if (left_value.can_be_POS() && right_value.can_be_NEG())
				n = true;
			if (left_value.can_be_POS() && right_value.can_be_ZERO())
				p = true;
			if (left_value.can_be_POS() && right_value.can_be_POS())
				p = true;
			return SignsAbstraction.getInstance().create(n, z, p);
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
		if (right instanceof SignsValue) {
			SignsValue left_value = (SignsValue) left;
			SignsValue right_value = (SignsValue) right;
			boolean p = false, n = false, z = false;
			if (left_value.can_be_NEG() && right_value.can_be_NEG())
				z = p = true;
			if (left_value.can_be_NEG() && right_value.can_be_ZERO())
				n = true;
			if (left_value.can_be_NEG() && right_value.can_be_POS())
				n = true;
			if (left_value.can_be_ZERO() && right_value.can_be_NEG())
				n = true;
			if (left_value.can_be_ZERO() && right_value.can_be_ZERO())
				z = true;
			if (left_value.can_be_ZERO() && right_value.can_be_POS())
				p = true;
			if (left_value.can_be_POS() && right_value.can_be_NEG())
				n = true;
			if (left_value.can_be_POS() && right_value.can_be_ZERO())
				p = true;
			if (left_value.can_be_POS() && right_value.can_be_POS())
				z = p = true;
			return SignsAbstraction.getInstance().create(n, z, p);
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

	@Override
	public AbstractValue _shift_left(AbstractValue left, AbstractValue right) {
		// Note that x << y considers only the least five bits of y
		// Three disjoint cases:
		// * x = ZERO, y in { NEG, ZERO, POS} => x << y = ZERO
		// * x in { NEG, ZERO, POS}, y = ZERO => x << y = x
		// * x in { NEG, POS }, y in { NEG, POS } => x << y = { POS, ZERO, NEG }		
		if (right instanceof SignsValue) {
			SignsValue left_value = (SignsValue) left;
			SignsValue right_value = (SignsValue) right;
			boolean p = false, n = false, z = left_value.can_be_ZERO();
			if (right_value.can_be_ZERO()) {
				n |= left_value.can_be_NEG();
				z |= left_value.can_be_ZERO();
				p |= left_value.can_be_POS();
			}
			if ((left_value.can_be_NEG() || left_value.can_be_POS())
					&& (right_value.can_be_NEG() || right_value
							.can_be_POS()))
				n = z = p = true;
			return SignsAbstraction.getInstance().create(n, z, p);
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

	@Override
	public AbstractValue _shift_right(AbstractValue left, AbstractValue right) {
		// Note that x >> y considers only the least five bits of y, sign of x is
		// preserved
		// Four disjoint cases:
		// * x = ZERO, y in { NEG, ZERO, POS } => x >> y = ZERO
		// * x in { NEG, ZERO, POS }, y = ZERO => x >> y = x
		// * x = POS, y in { NEG, POS } => x >> y = { ZERO, POS }
		// * x = NEG, y in { NEG, POS } => x >> y = NEG		
		if (right instanceof SignsValue) {
			SignsValue left_value = (SignsValue) left;
			SignsValue right_value = (SignsValue) right;
			boolean p = false, n = false, z = left_value.can_be_ZERO();
			if (right_value.can_be_ZERO()) {
				n |= left_value.can_be_NEG();
				z |= left_value.can_be_ZERO();
				p |= left_value.can_be_POS();
			}
			if (right_value.can_be_NEG() || right_value.can_be_POS()) {
				n |= left_value.can_be_NEG();
				z |= left_value.can_be_POS();
				p |= left_value.can_be_POS();
			}
			return SignsAbstraction.getInstance().create(n, z, p);
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

	@Override
	public AbstractValue _unsigned_shift_right(AbstractValue left, AbstractValue right) {
		// Note that x >>> y considers only the least five bits of y, sign of x is
		// not preserved
		// Three disjoint cases:
		// * x = ZERO, y in { NEG, ZERO, POS } => x >> y = ZERO
		// * x in { NEG, ZERO, POS }, y = ZERO => x >> y = x
		// * x in { NEG, POS }, y in { NEG, POS } => x >> y = { ZERO, POS }		
		if (right instanceof SignsValue) {
			SignsValue left_value = (SignsValue) left;
			SignsValue right_value = (SignsValue) right;
			boolean p = false, n = false, z = left_value.can_be_ZERO();
			if (right_value.can_be_ZERO()) {
				n |= left_value.can_be_NEG();
				z |= left_value.can_be_ZERO();
				p |= left_value.can_be_POS();
			}
			if ((left_value.can_be_NEG() || left_value.can_be_POS())
					&& (right_value.can_be_NEG() || right_value
							.can_be_POS()))
				z = p = true;
			return SignsAbstraction.getInstance().create(n, z, p);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _neg_impl(AbstractValue left) {
		SignsValue left_value = (SignsValue) left;

		return SignsAbstraction.getInstance().create(left_value.can_be_POS(), left_value.can_be_ZERO(), left_value.can_be_NEG());
	}

	@Override
	public AbstractBoolean _ge(AbstractValue left, AbstractValue right) {
		if (right instanceof SignsValue) {
			SignsValue left_value = (SignsValue) left;
			SignsValue right_value = (SignsValue) right;
			boolean t = false, f = false;
			if (left_value.can_be_NEG() && right_value.can_be_NEG())
				t = f = true;
			if (left_value.can_be_NEG() && right_value.can_be_ZERO())
				f = true;
			if (left_value.can_be_NEG() && right_value.can_be_POS())
				f = true;
			if (left_value.can_be_ZERO() && right_value.can_be_NEG())
				t = true;
			if (left_value.can_be_ZERO() && right_value.can_be_ZERO())
				t = true;
			if (left_value.can_be_ZERO() && right_value.can_be_POS())
				f = true;
			if (left_value.can_be_POS() && right_value.can_be_NEG())
				t = true;
			if (left_value.can_be_POS() && right_value.can_be_ZERO())
				t = true;
			if (left_value.can_be_POS() && right_value.can_be_POS())
				t = f = true;
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
		if (right instanceof SignsValue) {
			SignsValue left_value = (SignsValue) left;
			SignsValue right_value = (SignsValue) right;
			boolean t = false, f = false;
			if (left_value.can_be_NEG() && right_value.can_be_NEG())
				t = f = true;
			if (left_value.can_be_NEG() && right_value.can_be_ZERO())
				f = true;
			if (left_value.can_be_NEG() && right_value.can_be_POS())
				f = true;
			if (left_value.can_be_ZERO() && right_value.can_be_NEG())
				t = true;
			if (left_value.can_be_ZERO() && right_value.can_be_ZERO())
				f = true;
			if (left_value.can_be_ZERO() && right_value.can_be_POS())
				f = true;
			if (left_value.can_be_POS() && right_value.can_be_NEG())
				t = true;
			if (left_value.can_be_POS() && right_value.can_be_ZERO())
				t = true;
			if (left_value.can_be_POS() && right_value.can_be_POS())
				t = f = true;
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
		if (right instanceof SignsValue) {
			SignsValue left_value = (SignsValue) left;
			SignsValue right_value = (SignsValue) right;
			boolean t = false, f = false;
			if (left_value.can_be_NEG() && right_value.can_be_NEG())
				t = f = true;
			if (left_value.can_be_NEG() && right_value.can_be_ZERO())
				t = true;
			if (left_value.can_be_NEG() && right_value.can_be_POS())
				t = true;
			if (left_value.can_be_ZERO() && right_value.can_be_NEG())
				f = true;
			if (left_value.can_be_ZERO() && right_value.can_be_ZERO())
				t = true;
			if (left_value.can_be_ZERO() && right_value.can_be_POS())
				t = true;
			if (left_value.can_be_POS() && right_value.can_be_NEG())
				f = true;
			if (left_value.can_be_POS() && right_value.can_be_ZERO())
				f = true;
			if (left_value.can_be_POS() && right_value.can_be_POS())
				t = f = true;
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
		if (right instanceof SignsValue) {
			SignsValue left_value = (SignsValue) left;
			SignsValue right_value = (SignsValue) right;
			boolean t = false, f = false;
			if (left_value.can_be_NEG() && right_value.can_be_NEG())
				t = f = true;
			if (left_value.can_be_NEG() && right_value.can_be_ZERO())
				t = true;
			if (left_value.can_be_NEG() && right_value.can_be_POS())
				t = true;
			if (left_value.can_be_ZERO() && right_value.can_be_NEG())
				f = true;
			if (left_value.can_be_ZERO() && right_value.can_be_ZERO())
				f = true;
			if (left_value.can_be_ZERO() && right_value.can_be_POS())
				t = true;
			if (left_value.can_be_POS() && right_value.can_be_NEG())
				f = true;
			if (left_value.can_be_POS() && right_value.can_be_ZERO())
				f = true;
			if (left_value.can_be_POS() && right_value.can_be_POS())
				t = f = true;
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

	public AbstractBoolean _eq(AbstractValue left, AbstractValue right) {
		if (right instanceof SignsValue) {
			SignsValue left_value = (SignsValue) left;
			SignsValue right_value = (SignsValue) right;
			boolean t = (left_value.can_be_NEG() && right_value.can_be_NEG())
					|| (left_value.can_be_ZERO() && right_value.can_be_ZERO())
					|| (left_value.can_be_POS() && right_value.can_be_POS());
			boolean f = (left_value.can_be_NEG() && right_value.can_be_ZERO())
					|| (left_value.can_be_NEG() && right_value.can_be_POS())
					|| (left_value.can_be_ZERO() && right_value.can_be_NEG())
					|| (left_value.can_be_ZERO() && right_value.can_be_POS())
					|| (left_value.can_be_POS() && right_value.can_be_NEG())
					|| (left_value.can_be_POS() && right_value.can_be_ZERO());
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

	public AbstractBoolean _ne(AbstractValue left, AbstractValue right) {
		return _eq(left, right).not();
	}

	@Override
	public AbstractBoolean _ne(AbstractValue left, int right) {
		return _ne(left, left.abs.abstractMap(right));
	}

	@Override
	protected AbstractValue _minus_reverse(AbstractValue left, int right) {
		return _minus(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _minus_reverse(AbstractValue left, long right) {
		return _minus(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _minus_reverse(AbstractValue left, float right) {
		return _minus(left.abs.abstractMap(right), left);
	}

	@Override
	protected AbstractValue _minus_reverse(AbstractValue left, double right) {
		return _minus(left.abs.abstractMap(right), left);
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

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         Abstraction; Signs.NEG if this Abstraction is numerically less
	 *         than the operand; and Signs.POS if this Abstraction is
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
	 *         Abstraction; Signs.NEG if this Abstraction is numerically less
	 *         than the operand; and Signs.POS if this Abstraction is
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
	 *         Abstraction; Signs.NEG if this Abstraction is numerically less
	 *         than the operand; and Signs.POS if this Abstraction is
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
				&& this._lt(left, right) != AbstractBoolean.TRUE)
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

}
