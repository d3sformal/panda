//Copyright (C) 2012 United States Government as represented by the
//Administrator of the National Aeronautics and Space Administration
//(NASA).  All Rights Reserved.

//This software is distributed under the NASA Open Source Agreement
//(NOSA), version 1.3.  The NOSA has been approved by the Open Source
//Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
//directory tree for the complete NOSA document.

//THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
//KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
//LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
//SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
//A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
//THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
//DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.

package gov.nasa.jpf.abstraction.numeric;

import java.util.HashSet;
import java.util.Set;

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
public class Signs extends Abstraction {
	// basic tokens: NEG, ZERO, POS
	public static Signs NEG = new Signs(0);
	public static Signs ZERO = new Signs(1);
	public static Signs POS = new Signs(2);
	// composite tokens: NON_NEG, NON_ZERO, NON_POS, TOP
	public static Signs NON_NEG = new Signs(3);
	public static Signs NON_ZERO = new Signs(4);
	public static Signs NON_POS = new Signs(5);
	public static Signs TOP = new Signs(6);

	private static final int DOMAIN_SIZE = 3;
	
	public Signs(int key) {
		super(key);
	}

	/**
	 * @param isNeg  indicates whether the new abstraction can be negative
	 * @param isZero indicates whether the new abstraction can be zero
	 * @param isPos  indicates whether the new abstraction can be positive 
	 * @return the new abstraction with specified values
	 */		
	public static Signs create(boolean isNeg, boolean isZero,
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
	public Signs abstractMap(int v) {
		if (v > 0)
			return POS;
		if (v == 0)
			return ZERO;
		// if (v < 0)
		return NEG;
	}

	@Override
	public Signs abstractMap(long v) {
		if (v > 0)
			return POS;
		if (v == 0)
			return ZERO;
		// if (v < 0)
		return NEG;
	}

	@Override
	public Signs abstractMap(float v) {
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
	public Signs abstractMap(double v) {
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
	 * @return true, iff this abstraction is NEG, NON_ZERO, NON_POS or TOP
	 */
	public boolean can_be_NEG() {
		int key = this.getKey();
		return key == 0 || key == 4 || key == 5 || key == 6;
	}	

	/**
	 * @return true, iff this abstraction is ZERO, NON_NEG, NON_POS or TOP
	 */	
	public boolean can_be_ZERO() {
		int key = this.getKey();
		return key == 1 || key == 3 || key == 5 || key == 6;
	}

	/**
	 * @return true, iff this abstraction is POS, NON_NEG, NON_ZERO or TOP
	 */
	public boolean can_be_POS() {
		int key = this.getKey();
		return key == 2 || key == 3 || key == 4 || key == 6;
	}	
	
	@Override
	public Set<Abstraction> getTokens() {
		Set<Abstraction> tokens = new HashSet<Abstraction>();
		if (can_be_NEG())
			tokens.add(NEG);
		if (can_be_ZERO())
			tokens.add(ZERO);
		if (can_be_POS())
			tokens.add(POS);
		return tokens;
	}

	// returns possible tokens from TOP in order {NEG, ZERO, POS}
	@Override
	public Abstraction getToken(int idx) {
		int num = getTokensNumber();
		if (idx < 0 || idx >= num)
			throw new RuntimeException("### Error: out of range");
		if (can_be_NEG())
			if (idx == 0)
				return NEG;
			else if (can_be_ZERO())
				return (idx == 1) ? ZERO : POS;
			else
				return POS;
		else if (can_be_ZERO())
			return (idx == 0) ? ZERO : POS;
		else
			return POS;
	}

	@Override
	public int getTokensNumber() {
		int result = 0;
		if (can_be_POS())
			++result;
		if (can_be_NEG())
			++result;
		if (can_be_ZERO())
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
	
	@Override
	public Abstraction _plus(int right) {
		if (right == 1) {
			boolean n = can_be_NEG();
			boolean z = can_be_NEG();
			boolean p = can_be_ZERO() || can_be_POS();
			return create(n, z, p);
		} else if (right == -1) {
			boolean n = can_be_NEG() || can_be_ZERO();
			boolean z = can_be_POS();
			boolean p = can_be_POS();
			return create(n, z, p);
		} else
			return _plus(abstractMap(right));
	}

	@Override
	public Abstraction _plus(long right) {
		if (right == 1) {
			boolean n = can_be_NEG();
			boolean z = can_be_NEG();
			boolean p = can_be_ZERO() || can_be_POS();
			return create(n, z, p);
		} else if (right == -1) {
			boolean n = can_be_NEG() || can_be_ZERO();
			boolean z = can_be_POS();
			boolean p = can_be_POS();
			return create(n, z, p);
		} else
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
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean p = false, n = false, z = false;
			if (this.can_be_NEG() && right_value.can_be_NEG())
				n = true;
			if (this.can_be_NEG() && right_value.can_be_ZERO())
				n = true;
			if (this.can_be_NEG() && right_value.can_be_POS())
				n = z = p = true;
			if (this.can_be_ZERO() && right_value.can_be_NEG())
				n = true;
			if (this.can_be_ZERO() && right_value.can_be_ZERO())
				z = true;
			if (this.can_be_ZERO() && right_value.can_be_POS())
				p = true;
			if (this.can_be_POS() && right_value.can_be_NEG())
				n = z = p = true;
			if (this.can_be_POS() && right_value.can_be_ZERO())
				p = true;
			if (this.can_be_POS() && right_value.can_be_POS())
				p = true;
			return create(n, z, p);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _minus(Abstraction right) {
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean p = false, n = false, z = false;
			if (this.can_be_NEG() && right_value.can_be_NEG())
				n = z = p = true;
			if (this.can_be_NEG() && right_value.can_be_ZERO())
				n = true;
			if (this.can_be_NEG() && right_value.can_be_POS())
				n = true;
			if (this.can_be_ZERO() && right_value.can_be_NEG())
				p = true;
			if (this.can_be_ZERO() && right_value.can_be_ZERO())
				z = true;
			if (this.can_be_ZERO() && right_value.can_be_POS())
				n = true;
			if (this.can_be_POS() && right_value.can_be_NEG())
				p = true;
			if (this.can_be_POS() && right_value.can_be_ZERO())
				p = true;
			if (this.can_be_POS() && right_value.can_be_POS())
				n = z = p = true;
			return create(n, z, p);
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
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean p = false, n = false, z = false;
			if (this.can_be_NEG() && right_value.can_be_NEG())
				p = true;
			if (this.can_be_NEG() && right_value.can_be_ZERO())
				z = true;
			if (this.can_be_NEG() && right_value.can_be_POS())
				n = true;
			if (this.can_be_ZERO() && right_value.can_be_NEG())
				z = true;
			if (this.can_be_ZERO() && right_value.can_be_ZERO())
				z = true;
			if (this.can_be_ZERO() && right_value.can_be_POS())
				z = true;
			if (this.can_be_POS() && right_value.can_be_NEG())
				n = true;
			if (this.can_be_POS() && right_value.can_be_ZERO())
				z = true;
			if (this.can_be_POS() && right_value.can_be_POS())
				p = true;
			return create(n, z, p);
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

	@Override
	public Abstraction _div(Abstraction right) {
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			if (right_value.can_be_ZERO())
				throw new ArithmeticException(
						"Division by zero (in abstract IDIV)");
			boolean p = false, n = false, z = false;
			if (this.can_be_NEG() && right_value.can_be_NEG())
				p = true;
			if (this.can_be_NEG() && right_value.can_be_POS())
				n = true;
			if (this.can_be_ZERO() && right_value.can_be_NEG())
				z = true;
			if (this.can_be_ZERO() && right_value.can_be_POS())
				z = true;
			if (this.can_be_POS() && right_value.can_be_NEG())
				n = true;
			if (this.can_be_POS() && right_value.can_be_POS())
				p = true;
			return create(n, z, p);
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
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			if (right_value.can_be_ZERO())
				throw new ArithmeticException(
						"Division by zero (in abstract IDIV)");
			boolean p = false, n = false, z = false;
			if (this.can_be_NEG() && right_value.can_be_NEG())
				z = p = true;
			if (this.can_be_NEG() && right_value.can_be_POS())
				z = p = true;
			if (this.can_be_ZERO() && right_value.can_be_NEG())
				z = true;
			if (this.can_be_ZERO() && right_value.can_be_POS())
				z = true;
			if (this.can_be_POS() && right_value.can_be_NEG())
				z = p = true;
			if (this.can_be_POS() && right_value.can_be_POS())
				z = p = true;
			return create(n, z, p);
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
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean p = false, n = false, z = false;
			if (this.can_be_NEG() && right_value.can_be_NEG())
				n = true;
			if (this.can_be_NEG() && right_value.can_be_ZERO())
				z = true;
			if (this.can_be_NEG() && right_value.can_be_POS())
				z = p = true;
			if (this.can_be_ZERO() && right_value.can_be_NEG())
				z = true;
			if (this.can_be_ZERO() && right_value.can_be_ZERO())
				z = true;
			if (this.can_be_ZERO() && right_value.can_be_POS())
				z = true;
			if (this.can_be_POS() && right_value.can_be_NEG())
				z = p = true;
			if (this.can_be_POS() && right_value.can_be_ZERO())
				z = true;
			if (this.can_be_POS() && right_value.can_be_POS())
				z = p = true;
			return create(n, z, p);
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
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean p = false, n = false, z = false;
			if (this.can_be_NEG() && right_value.can_be_NEG())
				n = true;
			if (this.can_be_NEG() && right_value.can_be_ZERO())
				n = true;
			if (this.can_be_NEG() && right_value.can_be_POS())
				n = true;
			if (this.can_be_ZERO() && right_value.can_be_NEG())
				n = true;
			if (this.can_be_ZERO() && right_value.can_be_ZERO())
				z = true;
			if (this.can_be_ZERO() && right_value.can_be_POS())
				p = true;
			if (this.can_be_POS() && right_value.can_be_NEG())
				n = true;
			if (this.can_be_POS() && right_value.can_be_ZERO())
				p = true;
			if (this.can_be_POS() && right_value.can_be_POS())
				p = true;
			return create(n, z, p);
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
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean p = false, n = false, z = false;
			if (this.can_be_NEG() && right_value.can_be_NEG())
				z = p = true;
			if (this.can_be_NEG() && right_value.can_be_ZERO())
				n = true;
			if (this.can_be_NEG() && right_value.can_be_POS())
				n = true;
			if (this.can_be_ZERO() && right_value.can_be_NEG())
				n = true;
			if (this.can_be_ZERO() && right_value.can_be_ZERO())
				z = true;
			if (this.can_be_ZERO() && right_value.can_be_POS())
				p = true;
			if (this.can_be_POS() && right_value.can_be_NEG())
				n = true;
			if (this.can_be_POS() && right_value.can_be_ZERO())
				p = true;
			if (this.can_be_POS() && right_value.can_be_POS())
				z = p = true;
			return create(n, z, p);
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

	@Override
	public Abstraction _shift_left(Abstraction right) {
		// Note that x << y considers only the least five bits of y
		// Three disjoint cases:
		// * x = ZERO, y in { NEG, ZERO, POS} => x << y = ZERO
		// * x in { NEG, ZERO, POS}, y = ZERO => x << y = x
		// * x in { NEG, POS }, y in { NEG, POS } => x << y = { POS, ZERO, NEG }		
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean p = false, n = false, z = this.can_be_ZERO();
			if (right_value.can_be_ZERO()) {
				n |= this.can_be_NEG();
				z |= this.can_be_ZERO();
				p |= this.can_be_POS();
			}
			if ((this.can_be_NEG() || this.can_be_POS())
					&& (right_value.can_be_NEG() || right_value
							.can_be_POS()))
				n = z = p = true;
			return create(n, z, p);
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

	@Override
	public Abstraction _shift_right(Abstraction right) {
		// Note that x >> y considers only the least five bits of y, sign of x is
		// preserved
		// Four disjoint cases:
		// * x = ZERO, y in { NEG, ZERO, POS } => x >> y = ZERO
		// * x in { NEG, ZERO, POS }, y = ZERO => x >> y = x
		// * x = POS, y in { NEG, POS } => x >> y = { ZERO, POS }
		// * x = NEG, y in { NEG, POS } => x >> y = NEG		
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean p = false, n = false, z = this.can_be_ZERO();
			if (right_value.can_be_ZERO()) {
				n |= this.can_be_NEG();
				z |= this.can_be_ZERO();
				p |= this.can_be_POS();
			}
			if (right_value.can_be_NEG() || right_value.can_be_POS()) {
				n |= this.can_be_NEG();
				z |= this.can_be_POS();
				p |= this.can_be_POS();
			}
			return create(n, z, p);
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

	@Override
	public Abstraction _unsigned_shift_right(Abstraction right) {
		// Note that x >>> y considers only the least five bits of y, sign of x is
		// not preserved
		// Three disjoint cases:
		// * x = ZERO, y in { NEG, ZERO, POS } => x >> y = ZERO
		// * x in { NEG, ZERO, POS }, y = ZERO => x >> y = x
		// * x in { NEG, POS }, y in { NEG, POS } => x >> y = { ZERO, POS }		
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean p = false, n = false, z = this.can_be_ZERO();
			if (right_value.can_be_ZERO()) {
				n |= this.can_be_NEG();
				z |= this.can_be_ZERO();
				p |= this.can_be_POS();
			}
			if ((this.can_be_NEG() || this.can_be_POS())
					&& (right_value.can_be_NEG() || right_value
							.can_be_POS()))
				z = p = true;
			return create(n, z, p);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _neg() {
		return create(can_be_POS(), can_be_ZERO(), can_be_NEG());
	}

	@Override
	public AbstractBoolean _ge(Abstraction right) {
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean t = false, f = false;
			if (this.can_be_NEG() && right_value.can_be_NEG())
				t = f = true;
			if (this.can_be_NEG() && right_value.can_be_ZERO())
				f = true;
			if (this.can_be_NEG() && right_value.can_be_POS())
				f = true;
			if (this.can_be_ZERO() && right_value.can_be_NEG())
				t = true;
			if (this.can_be_ZERO() && right_value.can_be_ZERO())
				t = true;
			if (this.can_be_ZERO() && right_value.can_be_POS())
				f = true;
			if (this.can_be_POS() && right_value.can_be_NEG())
				t = true;
			if (this.can_be_POS() && right_value.can_be_ZERO())
				t = true;
			if (this.can_be_POS() && right_value.can_be_POS())
				t = f = true;
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
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean t = false, f = false;
			if (this.can_be_NEG() && right_value.can_be_NEG())
				t = f = true;
			if (this.can_be_NEG() && right_value.can_be_ZERO())
				f = true;
			if (this.can_be_NEG() && right_value.can_be_POS())
				f = true;
			if (this.can_be_ZERO() && right_value.can_be_NEG())
				t = true;
			if (this.can_be_ZERO() && right_value.can_be_ZERO())
				f = true;
			if (this.can_be_ZERO() && right_value.can_be_POS())
				f = true;
			if (this.can_be_POS() && right_value.can_be_NEG())
				t = true;
			if (this.can_be_POS() && right_value.can_be_ZERO())
				t = true;
			if (this.can_be_POS() && right_value.can_be_POS())
				t = f = true;
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
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean t = false, f = false;
			if (this.can_be_NEG() && right_value.can_be_NEG())
				t = f = true;
			if (this.can_be_NEG() && right_value.can_be_ZERO())
				t = true;
			if (this.can_be_NEG() && right_value.can_be_POS())
				t = true;
			if (this.can_be_ZERO() && right_value.can_be_NEG())
				f = true;
			if (this.can_be_ZERO() && right_value.can_be_ZERO())
				t = true;
			if (this.can_be_ZERO() && right_value.can_be_POS())
				t = true;
			if (this.can_be_POS() && right_value.can_be_NEG())
				f = true;
			if (this.can_be_POS() && right_value.can_be_ZERO())
				f = true;
			if (this.can_be_POS() && right_value.can_be_POS())
				t = f = true;
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
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean t = false, f = false;
			if (this.can_be_NEG() && right_value.can_be_NEG())
				t = f = true;
			if (this.can_be_NEG() && right_value.can_be_ZERO())
				t = true;
			if (this.can_be_NEG() && right_value.can_be_POS())
				t = true;
			if (this.can_be_ZERO() && right_value.can_be_NEG())
				f = true;
			if (this.can_be_ZERO() && right_value.can_be_ZERO())
				f = true;
			if (this.can_be_ZERO() && right_value.can_be_POS())
				t = true;
			if (this.can_be_POS() && right_value.can_be_NEG())
				f = true;
			if (this.can_be_POS() && right_value.can_be_ZERO())
				f = true;
			if (this.can_be_POS() && right_value.can_be_POS())
				t = f = true;
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
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean t = (this.can_be_NEG() && right_value.can_be_NEG())
					|| (this.can_be_ZERO() && right_value.can_be_ZERO())
					|| (this.can_be_POS() && right_value.can_be_POS());
			boolean f = (this.can_be_NEG() && right_value.can_be_ZERO())
					|| (this.can_be_NEG() && right_value.can_be_POS())
					|| (this.can_be_ZERO() && right_value.can_be_NEG())
					|| (this.can_be_ZERO() && right_value.can_be_POS())
					|| (this.can_be_POS() && right_value.can_be_NEG())
					|| (this.can_be_POS() && right_value.can_be_ZERO());
			if (f & t)
				return AbstractBoolean.TOP;
			else
				return (f) ? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
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

	@Override
	protected Abstraction _minus_reverse(int right) {
		return abstractMap(right)._minus(this);
	}

	@Override
	protected Abstraction _minus_reverse(long right) {
		return abstractMap(right)._minus(this);
	}

	@Override
	protected Abstraction _minus_reverse(float right) {
		return abstractMap(right)._minus(this);
	}

	@Override
	protected Abstraction _minus_reverse(double right) {
		return abstractMap(right)._minus(this);
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
	
	public String toString() {
		if (this instanceof Signs) {
			if (this.isComposite()) {
				String result = "";
				if (can_be_NEG())
					result = "NEG,";
				if (can_be_ZERO())
					result += (result.isEmpty()) ? "ZERO," : " ZERO,";
				if (can_be_POS())
					result += " POS";
				return "{ " + result + " }";
			}
			if (this == ZERO)
				return "ZERO";
			if (this == POS)
				return "POS";
			if (this == NEG)
				return "NEG";
		}
		throw new RuntimeException("## Error: unknown abstraction");
	}

}
