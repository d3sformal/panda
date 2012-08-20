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
 * The domain of this abstraction consists of two values: EVEN and ODD.
 * Numeric values are mapped to one of them depending on their remainder by modulo 2.
 * 
 * When the result of an abstract operation cannot be defined unambiguously
 * (e.g. ODD % ODD can be both ODD and EVEN), the special "composite token" TOP
 * returned.
 */
public class Evenness extends Abstraction {

	public static Evenness EVEN = new Evenness(0);
	public static Evenness ODD  = new Evenness(1);
	public static Evenness TOP = new Evenness(2);

	public boolean can_be_ODD() {
		return getKey() != 0;
	}

	public boolean can_be_EVEN() {
		return getKey() != 1;
	}

	@Override
	public Set<Abstraction> getTokens() {
		Set<Abstraction> tokens = new HashSet<Abstraction>();
		if (can_be_EVEN())
			tokens.add(EVEN);
		if (can_be_ODD())
			tokens.add(ODD);
		return tokens;
	}

	// returns possible tokens from TOP in order {EVEN, ODD}
	@Override
	public Abstraction getToken(int key) {
		int num = getTokensNumber();
		if (key < 0 || key >= num)
			throw new RuntimeException("Wrong TOP token");
		if (can_be_EVEN())
			return (key == 0)? EVEN : ODD;
		else
			return ODD;
	}

	@Override
	public int getTokensNumber() {
		int result = 0;
		if (can_be_EVEN())
			++result;
		if (can_be_ODD())
			++result;
		return result;
	}

	public Evenness(int key) {
		super(key);
	}

	private Evenness create(boolean isEven, boolean isOdd) {
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
	public boolean isComposite() {
		return this == TOP;
	}	
	
	@Override
	public Evenness abstractMap(int v) {
		if (v % 2 == 0)
			return EVEN;
		else
			return ODD;
	}

	@Override
	public Evenness abstractMap(long v) {
		if (v % 2 == 0)
			return EVEN;
		else
			return ODD;
	}

	@Override
	public Abstraction _plus(int right) {
		if (right == 1 || right == -1)
			return create(can_be_ODD(), can_be_EVEN());
		else
			return _plus(abstractMap(right));
	}

	@Override
	public Abstraction _plus(long right) {
		if (right == 1 || right == -1)
			return create(can_be_ODD(), can_be_EVEN());
		else
			return _plus(abstractMap(right));
	}
	
	@Override
	public Abstraction _plus(Abstraction right) {
		if (right instanceof Evenness) {			
			Evenness right_value = (Evenness) right;	
			boolean o = false, e = false;			
			if (this.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (this.can_be_EVEN() && right_value.can_be_ODD())
				o = true;		
			if (this.can_be_ODD() && right_value.can_be_EVEN())
				o = true;
			if (this.can_be_ODD() && right_value.can_be_ODD())
				e = true;			
			return create(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _minus(Abstraction right) {
		if (right instanceof Evenness) {			
			Evenness right_value = (Evenness) right;	
			boolean o = false, e = false;			
			if (this.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (this.can_be_EVEN() && right_value.can_be_ODD())
				o = true;		
			if (this.can_be_ODD() && right_value.can_be_EVEN())
				o = true;
			if (this.can_be_ODD() && right_value.can_be_ODD())
				e = true;			
			return create(e, o);
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
	public Abstraction _minus_reverse(int right) {
		return abstractMap(right)._minus(this);
	}

	@Override
	public Abstraction _minus_reverse(long right) {
		return abstractMap(right)._minus(this);
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
	public Abstraction _mul(Abstraction right) {
		if (right instanceof Evenness) {			
			Evenness right_value = (Evenness) right;	
			boolean o = false, e = false;			
			if (this.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (this.can_be_EVEN() && right_value.can_be_ODD())
				e = true;		
			if (this.can_be_ODD() && right_value.can_be_EVEN())
				e = true;
			if (this.can_be_ODD() && right_value.can_be_ODD())
				o = true;			
			return create(e, o);
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
	public Abstraction _div(Abstraction right) {
		if (right instanceof Evenness)	
			return TOP;
		else
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
	public Abstraction _rem(Abstraction right) {
		if (right instanceof Evenness) {			
			Evenness right_value = (Evenness) right;	
			boolean o = false, e = false;			
			if (this.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (this.can_be_EVEN() && right_value.can_be_ODD())
				e = o = true;		
			if (this.can_be_ODD() && right_value.can_be_EVEN())
				o = true;
			if (this.can_be_ODD() && right_value.can_be_ODD())
				e = o = true;			
			return create(e, o);
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
		if (right instanceof Evenness) {			
			Evenness right_value = (Evenness) right;	
			boolean o = false, e = false;			
			if (this.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (this.can_be_EVEN() && right_value.can_be_ODD())
				e = true;		
			if (this.can_be_ODD() && right_value.can_be_EVEN())
				e = true;
			if (this.can_be_ODD() && right_value.can_be_ODD())
				o = true;			
			return create(e, o);
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
		if (right instanceof Evenness) {			
			Evenness right_value = (Evenness) right;	
			boolean o = false, e = false;			
			if (this.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (this.can_be_EVEN() && right_value.can_be_ODD())
				o = true;		
			if (this.can_be_ODD() && right_value.can_be_EVEN())
				o = true;
			if (this.can_be_ODD() && right_value.can_be_ODD())
				o = true;			
			return create(e, o);
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
		if (right instanceof Evenness) {			
			Evenness right_value = (Evenness) right;	
			boolean o = false, e = false;			
			if (this.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (this.can_be_EVEN() && right_value.can_be_ODD())
				o = true;		
			if (this.can_be_ODD() && right_value.can_be_EVEN())
				o = true;
			if (this.can_be_ODD() && right_value.can_be_ODD())
				e = true;			
			return create(e, o);
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
		if (right instanceof Evenness) {			
			Evenness right_value = (Evenness) right;	
			return create(true, this.can_be_ODD() && right_value.can_be_ODD());
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
		if (right instanceof Evenness)			
			return TOP;
		else
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
		// sign does not matter
		return _shift_right(right);
	}

	@Override
	public Abstraction _neg() {
		return this;
	}

	@Override
	public AbstractBoolean _ge(Abstraction right) {
		if (right instanceof Evenness)
			return AbstractBoolean.TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _ge(int right) {
		return _ge(abstractMap(right));
	}

	@Override
	public AbstractBoolean _gt(Abstraction right) {
		if (right instanceof Evenness)
			return AbstractBoolean.TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _gt(int right) {
		return _gt(abstractMap(right));
	}

	@Override
	public AbstractBoolean _le(Abstraction right) {
		if (right instanceof Evenness)
			return AbstractBoolean.TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _le(int right) {
		return _le(abstractMap(right));
	}

	@Override
	public AbstractBoolean _lt(Abstraction right) {
		if (right instanceof Evenness)
			return AbstractBoolean.TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _lt(int right) {
		return _lt(abstractMap(right));
	}

	public AbstractBoolean _eq(Abstraction right) {
		if (right instanceof Evenness) {
			Evenness right_value = (Evenness) right;
			boolean t = (this.can_be_EVEN() && right_value.can_be_EVEN()) ||
					(this.can_be_ODD() && right_value.can_be_ODD());
			boolean f = (this.can_be_EVEN() && right_value.can_be_ODD()) ||
					(this.can_be_ODD() && right_value.can_be_EVEN());
			assert t || f;
			if (t & f)
				return AbstractBoolean.TOP;
			else if (t)
				return AbstractBoolean.TRUE;
			else
				return AbstractBoolean.TRUE;
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
		if (this instanceof Evenness) {
			if (this == EVEN)
				return "EVEN";
			if (this == ODD)
				return "ODD";
			if (this.isComposite())
				return "TOP";
		} else
			throw new RuntimeException("## Error: unknown abstraction");
		return "";
	}
		
}
