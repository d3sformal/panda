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
	
	@Override
	public AbstractValue _plus(AbstractValue left, int right) {
		EvennessValue left_val = (EvennessValue) left;

		if (right == 1 || right == -1)
			return EvennessAbstraction.getInstance().create(left_val.can_be_ODD(), left_val.can_be_EVEN());
		else
			return _plus(left, left.abs.abstractMap(right));
	}

	@Override
	public AbstractValue _plus(AbstractValue left, long right) {
		EvennessValue left_val = (EvennessValue) left;

		if (right == 1 || right == -1)
			return EvennessAbstraction.getInstance().create(left_val.can_be_ODD(), left_val.can_be_EVEN());
		else
			return _plus(left, left.abs.abstractMap(right));
	}
	
	@Override
	public AbstractValue _plus(AbstractValue left, AbstractValue right) {
		if (right instanceof EvennessValue) {
			EvennessValue left_value = (EvennessValue) left;
			EvennessValue right_value = (EvennessValue) right;	
			boolean o = false, e = false;			
			if (left_value.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (left_value.can_be_EVEN() && right_value.can_be_ODD())
				o = true;		
			if (left_value.can_be_ODD() && right_value.can_be_EVEN())
				o = true;
			if (left_value.can_be_ODD() && right_value.can_be_ODD())
				e = true;			
			return EvennessAbstraction.getInstance().create(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _minus(AbstractValue left, AbstractValue right) {
		if (right instanceof EvennessValue) {
			EvennessValue left_value = (EvennessValue) left;
			EvennessValue right_value = (EvennessValue) right;	
			boolean o = false, e = false;			
			if (left_value.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (left_value.can_be_EVEN() && right_value.can_be_ODD())
				o = true;		
			if (left_value.can_be_ODD() && right_value.can_be_EVEN())
				o = true;
			if (left_value.can_be_ODD() && right_value.can_be_ODD())
				e = true;			
			return EvennessAbstraction.getInstance().create(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}
	
	@Override
	public AbstractValue _mul(AbstractValue left, AbstractValue right) {
		if (right instanceof EvennessValue) {
			EvennessValue left_value = (EvennessValue) left;
			EvennessValue right_value = (EvennessValue) right;	
			boolean o = false, e = false;			
			if (left_value.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (left_value.can_be_EVEN() && right_value.can_be_ODD())
				e = true;		
			if (left_value.can_be_ODD() && right_value.can_be_EVEN())
				e = true;
			if (left_value.can_be_ODD() && right_value.can_be_ODD())
				o = true;			
			return EvennessAbstraction.getInstance().create(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _div(AbstractValue left, AbstractValue right) {
		if (right instanceof EvennessValue)	
			return EvennessAbstraction.TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _rem(AbstractValue left, AbstractValue right) {
		if (right instanceof EvennessValue) {
			EvennessValue left_value = (EvennessValue) left;
			EvennessValue right_value = (EvennessValue) right;	
			boolean o = false, e = false;			
			if (left_value.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (left_value.can_be_EVEN() && right_value.can_be_ODD())
				e = o = true;		
			if (left_value.can_be_ODD() && right_value.can_be_EVEN())
				o = true;
			if (left_value.can_be_ODD() && right_value.can_be_ODD())
				e = o = true;			
			return EvennessAbstraction.getInstance().create(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _bitwise_and(AbstractValue left, AbstractValue right) {
		if (right instanceof EvennessValue) {
			EvennessValue left_value = (EvennessValue) left;
			EvennessValue right_value = (EvennessValue) right;	
			boolean o = false, e = false;			
			if (left_value.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (left_value.can_be_EVEN() && right_value.can_be_ODD())
				e = true;		
			if (left_value.can_be_ODD() && right_value.can_be_EVEN())
				e = true;
			if (left_value.can_be_ODD() && right_value.can_be_ODD())
				o = true;			
			return EvennessAbstraction.getInstance().create(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _bitwise_or(AbstractValue left, AbstractValue right) {
		if (right instanceof EvennessValue) {
			EvennessValue left_value = (EvennessValue) left;
			EvennessValue right_value = (EvennessValue) right;	
			boolean o = false, e = false;			
			if (left_value.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (left_value.can_be_EVEN() && right_value.can_be_ODD())
				o = true;		
			if (left_value.can_be_ODD() && right_value.can_be_EVEN())
				o = true;
			if (left_value.can_be_ODD() && right_value.can_be_ODD())
				o = true;			
			return EvennessAbstraction.getInstance().create(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");	
	}

	@Override
	public AbstractValue _bitwise_xor(AbstractValue left, AbstractValue right) {
		if (right instanceof EvennessValue) {
			EvennessValue left_value = (EvennessValue) left;
			EvennessValue right_value = (EvennessValue) right;	
			boolean o = false, e = false;			
			if (left_value.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (left_value.can_be_EVEN() && right_value.can_be_ODD())
				o = true;		
			if (left_value.can_be_ODD() && right_value.can_be_EVEN())
				o = true;
			if (left_value.can_be_ODD() && right_value.can_be_ODD())
				e = true;			
			return EvennessAbstraction.getInstance().create(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _shift_left(AbstractValue left, AbstractValue right) {
		if (right instanceof EvennessValue) {
			EvennessValue left_value = (EvennessValue) left;
			EvennessValue right_value = (EvennessValue) right;	
			return EvennessAbstraction.getInstance().create(true, left_value.can_be_ODD() && right_value.can_be_ODD());
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}
 
	@Override
	public AbstractValue _shift_right(AbstractValue left, AbstractValue right) {
		if (right instanceof EvennessValue)			
			return EvennessAbstraction.TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _unsigned_shift_right(AbstractValue left, AbstractValue right) {
		// sign does not matter
		return _shift_right(left, right);
	}

	@Override
	public AbstractValue _neg_impl(AbstractValue left) {
		return left;
	}

	@Override
	public AbstractBoolean _ge(AbstractValue left, AbstractValue right) {
		if (right instanceof EvennessValue)
			return AbstractBoolean.TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _gt(AbstractValue left, AbstractValue right) {
		if (right instanceof EvennessValue)
			return AbstractBoolean.TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _le(AbstractValue left, AbstractValue right) {
		if (right instanceof EvennessValue)
			return AbstractBoolean.TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _lt(AbstractValue left, AbstractValue right) {
		if (right instanceof EvennessValue)
			return AbstractBoolean.TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _eq(AbstractValue left, AbstractValue right) {
		if (right instanceof EvennessValue) {
			EvennessValue left_value = (EvennessValue) left;
			EvennessValue right_value = (EvennessValue) right;
			boolean t = (left_value.can_be_EVEN() && right_value.can_be_EVEN()) ||
					(left_value.can_be_ODD() && right_value.can_be_ODD());
			boolean f = (left_value.can_be_EVEN() && right_value.can_be_ODD()) ||
					(left_value.can_be_ODD() && right_value.can_be_EVEN());
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
				&& _lt(left, right) != AbstractBoolean.TRUE)
			z = true;
		return SignsAbstraction.getInstance().create(n, z, p);
	}
		
}
