package gov.nasa.jpf.abstraction.numeric;

import java.util.HashSet;
import java.util.Set;

public class EvennessValue extends AbstractValue {
	
	public EvennessValue(int key) {
		super(key);
		abs = Evenness.getInstance();
	}
	
	public boolean can_be_ODD() {
		return getKey() != 0;
	}

	public boolean can_be_EVEN() {
		return getKey() != 1;
	}

	@Override
	public Set<AbstractValue> getTokens() {
		Set<AbstractValue> tokens = new HashSet<AbstractValue>();
		if (can_be_EVEN())
			tokens.add(Evenness.EVEN);
		if (can_be_ODD())
			tokens.add(Evenness.ODD);
		return tokens;
	}

	// returns possible tokens from TOP in order {EVEN, ODD}
	@Override
	public AbstractValue getToken(int key) {
		int num = getTokensNumber();
		if (key < 0 || key >= num)
			throw new RuntimeException("Wrong TOP token");
		if (can_be_EVEN())
			return (key == 0)? Evenness.EVEN : Evenness.ODD;
		else
			return Evenness.ODD;
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
	
	@Override
	public boolean isComposite() {
		return this == Evenness.TOP;
	}
	
	@Override
	public AbstractValue _plus(int right) {
		if (right == 1 || right == -1)
			return Evenness.getInstance().create(can_be_ODD(), can_be_EVEN());
		else
			return _plus(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _plus(long right) {
		if (right == 1 || right == -1)
			return Evenness.getInstance().create(can_be_ODD(), can_be_EVEN());
		else
			return _plus(abs.abstractMap(right));
	}
	
	@Override
	public AbstractValue _plus(AbstractValue right) {
		if (right instanceof EvennessValue) {			
			EvennessValue right_value = (EvennessValue) right;	
			boolean o = false, e = false;			
			if (this.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (this.can_be_EVEN() && right_value.can_be_ODD())
				o = true;		
			if (this.can_be_ODD() && right_value.can_be_EVEN())
				o = true;
			if (this.can_be_ODD() && right_value.can_be_ODD())
				e = true;			
			return Evenness.getInstance().create(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _minus(AbstractValue right) {
		if (right instanceof EvennessValue) {			
			EvennessValue right_value = (EvennessValue) right;	
			boolean o = false, e = false;			
			if (this.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (this.can_be_EVEN() && right_value.can_be_ODD())
				o = true;		
			if (this.can_be_ODD() && right_value.can_be_EVEN())
				o = true;
			if (this.can_be_ODD() && right_value.can_be_ODD())
				e = true;			
			return Evenness.getInstance().create(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _minus(int right) {
		return _minus(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _minus(long right) {
		return _minus(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _minus_reverse(int right) {
		return abs.abstractMap(right)._minus(this);
	}

	@Override
	public AbstractValue _minus_reverse(long right) {
		return abs.abstractMap(right)._minus(this);
	}
	
	@Override
	public AbstractValue _mul(int right) {
		return _mul(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _mul(long right) {
		return _mul(abs.abstractMap(right));
	}
	
	@Override
	public AbstractValue _mul(AbstractValue right) {
		if (right instanceof EvennessValue) {			
			EvennessValue right_value = (EvennessValue) right;	
			boolean o = false, e = false;			
			if (this.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (this.can_be_EVEN() && right_value.can_be_ODD())
				e = true;		
			if (this.can_be_ODD() && right_value.can_be_EVEN())
				e = true;
			if (this.can_be_ODD() && right_value.can_be_ODD())
				o = true;			
			return Evenness.getInstance().create(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _div(int right) {
		return _div(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _div(long right) {
		return _div(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _div(AbstractValue right) {
		if (right instanceof EvennessValue)	
			return Evenness.TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _rem(int right) {
		return _rem(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _rem(long right) {
		return _rem(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _rem(AbstractValue right) {
		if (right instanceof EvennessValue) {			
			EvennessValue right_value = (EvennessValue) right;	
			boolean o = false, e = false;			
			if (this.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (this.can_be_EVEN() && right_value.can_be_ODD())
				e = o = true;		
			if (this.can_be_ODD() && right_value.can_be_EVEN())
				o = true;
			if (this.can_be_ODD() && right_value.can_be_ODD())
				e = o = true;			
			return Evenness.getInstance().create(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _bitwise_and(int right) {
		return _bitwise_and(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _bitwise_and(long right) {
		return _bitwise_and(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _bitwise_and(AbstractValue right) {
		if (right instanceof EvennessValue) {			
			EvennessValue right_value = (EvennessValue) right;	
			boolean o = false, e = false;			
			if (this.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (this.can_be_EVEN() && right_value.can_be_ODD())
				e = true;		
			if (this.can_be_ODD() && right_value.can_be_EVEN())
				e = true;
			if (this.can_be_ODD() && right_value.can_be_ODD())
				o = true;			
			return Evenness.getInstance().create(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _bitwise_or(int right) {
		return _bitwise_or(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _bitwise_or(long right) {
		return _bitwise_or(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _bitwise_or(AbstractValue right) {
		if (right instanceof EvennessValue) {			
			EvennessValue right_value = (EvennessValue) right;	
			boolean o = false, e = false;			
			if (this.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (this.can_be_EVEN() && right_value.can_be_ODD())
				o = true;		
			if (this.can_be_ODD() && right_value.can_be_EVEN())
				o = true;
			if (this.can_be_ODD() && right_value.can_be_ODD())
				o = true;			
			return Evenness.getInstance().create(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");	
	}

	@Override
	public AbstractValue _bitwise_xor(int right) {
		return _bitwise_xor(abs.abstractMap(right));
	}
	
	@Override
	public AbstractValue _bitwise_xor(long right) {
		return _bitwise_xor(abs.abstractMap(right));
	}	

	@Override
	public AbstractValue _bitwise_xor(AbstractValue right) {
		if (right instanceof EvennessValue) {			
			EvennessValue right_value = (EvennessValue) right;	
			boolean o = false, e = false;			
			if (this.can_be_EVEN() && right_value.can_be_EVEN())
				e = true;
			if (this.can_be_EVEN() && right_value.can_be_ODD())
				o = true;		
			if (this.can_be_ODD() && right_value.can_be_EVEN())
				o = true;
			if (this.can_be_ODD() && right_value.can_be_ODD())
				e = true;			
			return Evenness.getInstance().create(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _shift_left(int right) {
		return _shift_left(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _shift_left(long right) {
		return _shift_left(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _shift_left(AbstractValue right) {
		if (right instanceof EvennessValue) {			
			EvennessValue right_value = (EvennessValue) right;	
			return Evenness.getInstance().create(true, this.can_be_ODD() && right_value.can_be_ODD());
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _shift_right(int right) {
		return _shift_right(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _shift_right(long right) {
		return _shift_right(abs.abstractMap(right));
	}
 
	@Override
	public AbstractValue _shift_right(AbstractValue right) {
		if (right instanceof EvennessValue)			
			return Evenness.TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _unsigned_shift_right(int right) {
		return _unsigned_shift_right(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _unsigned_shift_right(long right) {
		return _unsigned_shift_right(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _unsigned_shift_right(AbstractValue right) {
		// sign does not matter
		return _shift_right(right);
	}

	@Override
	public AbstractValue _neg() {
		return this;
	}

	@Override
	public AbstractBoolean _ge(AbstractValue right) {
		if (right instanceof EvennessValue)
			return AbstractBoolean.TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _ge(int right) {
		return _ge(abs.abstractMap(right));
	}

	@Override
	public AbstractBoolean _gt(AbstractValue right) {
		if (right instanceof EvennessValue)
			return AbstractBoolean.TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _gt(int right) {
		return _gt(abs.abstractMap(right));
	}

	@Override
	public AbstractBoolean _le(AbstractValue right) {
		if (right instanceof EvennessValue)
			return AbstractBoolean.TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _le(int right) {
		return _le(abs.abstractMap(right));
	}

	@Override
	public AbstractBoolean _lt(AbstractValue right) {
		if (right instanceof EvennessValue)
			return AbstractBoolean.TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _lt(int right) {
		return _lt(abs.abstractMap(right));
	}

	public AbstractBoolean _eq(AbstractValue right) {
		if (right instanceof EvennessValue) {
			EvennessValue right_value = (EvennessValue) right;
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
		return _eq(abs.abstractMap(right));
	}		
	
	public AbstractBoolean _ne(AbstractValue right) {
		return _eq(right).not();
	}

	@Override
	public AbstractBoolean _ne(int right) {
		return _ne(abs.abstractMap(right));
	}
	
	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         Abstraction; Signs.NEG if this Abstraction is numerically less
	 *         than the operand; and Signs.POS if this Abstraction is
	 *         numerically greater than the operand.
	 */
	@Override
	public AbstractValue _cmp(AbstractValue right) {
		boolean n = false, z = false, p = false;
		if (this._gt(right) != AbstractBoolean.FALSE)
			p = true;
		if (this._lt(right) != AbstractBoolean.FALSE)
			n = true;
		if (this._gt(right) != AbstractBoolean.TRUE
				&& this._lt(right) != AbstractBoolean.TRUE)
			z = true;
		return Signs.getInstance().create(n, z, p);
	}

	@Override
	public AbstractValue _cmp(long right) {
		return this._cmp(abs.abstractMap(right));
	}

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         Abstraction; Signs.NEG if this Abstraction is numerically less
	 *         than the operand; and Signs.POS if this Abstraction is
	 *         numerically greater than the operand.
	 */	
	@Override
	public AbstractValue _cmpg(AbstractValue right) {
		boolean n = false, z = false, p = false;
		if (this._gt(right) != AbstractBoolean.FALSE)
			p = true;
		if (this._lt(right) != AbstractBoolean.FALSE)
			n = true;
		if (this._gt(right) != AbstractBoolean.TRUE
				&& this._lt(right) != AbstractBoolean.TRUE)
			z = true;
		return Signs.getInstance().create(n, z, p);
	}

	@Override
	public AbstractValue _cmpg(float right) {
		return this._cmpg(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _cmpg(double right) {
		return this._cmpg(abs.abstractMap(right));
	}

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         Abstraction; Signs.NEG if this Abstraction is numerically less
	 *         than the operand; and Signs.POS if this Abstraction is
	 *         numerically greater than the operand.
	 */	
	@Override
	public AbstractValue _cmpl(AbstractValue right) {
		boolean n = false, z = false, p = false;
		if (this._gt(right) != AbstractBoolean.FALSE)
			p = true;
		if (this._lt(right) != AbstractBoolean.FALSE)
			n = true;
		if (this._gt(right) != AbstractBoolean.TRUE
				&& this._lt(right) != AbstractBoolean.TRUE)
			z = true;
		return Signs.getInstance().create(n, z, p);
	}

	@Override
	public AbstractValue _cmpl(float right) {
		return this._cmpl(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _cmpl(double right) {
		return this._cmpl(abs.abstractMap(right));
	}	

	@Override
	protected AbstractValue _div_reverse(int right) {
		return abs.abstractMap(right)._div(this);
	}

	@Override
	protected AbstractValue _div_reverse(long right) {
		return abs.abstractMap(right)._div(this);
	}

	@Override
	protected AbstractValue _div_reverse(float right) {
		return abs.abstractMap(right)._div(this);
	}

	@Override
	protected AbstractValue _div_reverse(double right) {
		return abs.abstractMap(right)._div(this);
	}

	@Override
	protected AbstractValue _cmp_reverse(long right) {
		return abs.abstractMap(right)._cmp(this);
	}		
	
	@Override
	protected AbstractValue _cmpl_reverse(float right) {
		return abs.abstractMap(right)._cmpl(this);
	}

	@Override
	protected AbstractValue _cmpl_reverse(double right) {
		return abs.abstractMap(right)._cmpl(this);
	}

	@Override
	protected AbstractValue _cmpg_reverse(float right) {
		return abs.abstractMap(right)._cmpg(this);
	}

	@Override
	protected AbstractValue _cmpg_reverse(double right) {
		return abs.abstractMap(right)._cmpg(this);
	}

	@Override
	protected AbstractValue _rem_reverse(int right) {
		return abs.abstractMap(right)._rem(this);
	}

	@Override
	protected AbstractValue _rem_reverse(long right) {
		return abs.abstractMap(right)._rem(this);
	}

	@Override
	protected AbstractValue _rem_reverse(float right) {
		return abs.abstractMap(right)._rem(this);
	}

	@Override
	protected AbstractValue _rem_reverse(double right) {
		return abs.abstractMap(right)._rem(this);
	}

	@Override
	protected AbstractValue _shift_left_reverse(int right) {
		return abs.abstractMap(right)._shift_left(this);
	}

	@Override
	protected AbstractValue _shift_left_reverse(long right) {
		return abs.abstractMap(right)._shift_left(this);
	}

	@Override
	protected AbstractValue _shift_right_reverse(int right) {
		return abs.abstractMap(right)._shift_right(this);
	}

	@Override
	protected AbstractValue _shift_right_reverse(long right) {
		return abs.abstractMap(right)._shift_right(this);
	}

	@Override
	protected AbstractValue _unsigned_shift_right_reverse(int right) {
		return abs.abstractMap(right)._unsigned_shift_right(this);
	}

	@Override
	protected AbstractValue _unsigned_shift_right_reverse(long right) {
		return abs.abstractMap(right)._unsigned_shift_right(this);
	}

	@Override
	protected AbstractBoolean _lt_reverse(int right) {
		return abs.abstractMap(right)._lt(this);
	}

	@Override
	protected AbstractBoolean _le_reverse(int right) {
		return abs.abstractMap(right)._le(this);
	}

	@Override
	protected AbstractBoolean _ge_reverse(int right) {
		return abs.abstractMap(right)._ge(this);
	}

	@Override
	protected AbstractBoolean _gt_reverse(int right) {
		return abs.abstractMap(right)._gt(this);
	}
	
	public String toString() {
		if (this == Evenness.EVEN)
			return "EVEN";
		if (this == Evenness.ODD)
			return "ODD";
		if (this.isComposite())
			return "TOP";

		return "";
	}
}
