package gov.nasa.jpf.abstraction.numeric;

import java.util.HashSet;
import java.util.Set;

public class SignsValue extends AbstractValue {
	public SignsValue(int key) {
		super(key);
		abs = Signs.getInstance();
	}
	
	/**
	 * @return true, iff this abstraction is NEG, NON_ZERO, NON_POS or TOP
	 */
	public boolean can_be_NEG() {
		int key = getKey();
		return key == 0 || key == 4 || key == 5 || key == 6;
	}	

	/**
	 * @return true, iff this abstraction is ZERO, NON_NEG, NON_POS or TOP
	 */	
	public boolean can_be_ZERO() {
		int key = getKey();
		return key == 1 || key == 3 || key == 5 || key == 6;
	}

	/**
	 * @return true, iff this abstraction is POS, NON_NEG, NON_ZERO or TOP
	 */
	public boolean can_be_POS() {
		int key = getKey();
		return key == 2 || key == 3 || key == 4 || key == 6;
	}
	
	@Override
	public Set<AbstractValue> getTokens() {
		Set<AbstractValue> tokens = new HashSet<AbstractValue>();
		if (can_be_NEG())
			tokens.add(Signs.NEG);
		if (can_be_ZERO())
			tokens.add(Signs.ZERO);
		if (can_be_POS())
			tokens.add(Signs.POS);
		return tokens;
	}

	// returns possible tokens from TOP in order {NEG, ZERO, POS}
	@Override
	public AbstractValue getToken(int idx) {
		int num = getTokensNumber();
		if (idx < 0 || idx >= num)
			throw new RuntimeException("### Error: out of range");
		if (can_be_NEG())
			if (idx == 0)
				return Signs.NEG;
			else if (can_be_ZERO())
				return (idx == 1) ? Signs.ZERO : Signs.POS;
			else
				return Signs.POS;
		else if (can_be_ZERO())
			return (idx == 0) ? Signs.ZERO : Signs.POS;
		else
			return Signs.POS;
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
	 * @return true, if this abstraction is a single value from the domain;
	 * false, if this abstraction represents a set of values from the domain.
	 */	
	@Override
	public boolean isComposite() {
		return getKey() > 2;
	}
	
	@Override
	public AbstractValue _plus(int right) {
		if (right == 1) {
			boolean n = can_be_NEG();
			boolean z = can_be_NEG();
			boolean p = can_be_ZERO() || can_be_POS();
			return Signs.getInstance().create(n, z, p);
		} else if (right == -1) {
			boolean n = can_be_NEG() || can_be_ZERO();
			boolean z = can_be_POS();
			boolean p = can_be_POS();
			return Signs.getInstance().create(n, z, p);
		} else
			return _plus(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _plus(long right) {
		if (right == 1) {
			boolean n = can_be_NEG();
			boolean z = can_be_NEG();
			boolean p = can_be_ZERO() || can_be_POS();
			return Signs.getInstance().create(n, z, p);
		} else if (right == -1) {
			boolean n = can_be_NEG() || can_be_ZERO();
			boolean z = can_be_POS();
			boolean p = can_be_POS();
			return Signs.getInstance().create(n, z, p);
		} else
			return _plus(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _plus(float right) {
		return _plus(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _plus(double right) {
		return _plus(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _plus(AbstractValue right) {
		if (right instanceof SignsValue) {
			SignsValue right_value = (SignsValue) right;
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
			return Signs.getInstance().create(n, z, p);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _minus(AbstractValue right) {
		if (right instanceof SignsValue) {
			SignsValue right_value = (SignsValue) right;
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
			return Signs.getInstance().create(n, z, p);
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
	public AbstractValue _minus(float right) {
		return _minus(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _minus(double right) {
		return _minus(abs.abstractMap(right));
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
	public AbstractValue _mul(float right) {
		return _mul(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _mul(double right) {
		return _mul(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _mul(AbstractValue right) {
		if (right instanceof SignsValue) {
			SignsValue right_value = (SignsValue) right;
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
			return Signs.getInstance().create(n, z, p);
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
	public AbstractValue _div(float right) {
		return _div(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _div(double right) {
		return _div(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _div(AbstractValue right) {
		if (right instanceof SignsValue) {
			SignsValue right_value = (SignsValue) right;
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
			return Signs.getInstance().create(n, z, p);
		} else
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
	public AbstractValue _rem(float right) {
		return _rem(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _rem(double right) {
		return _rem(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _rem(AbstractValue right) {
		if (right instanceof SignsValue) {
			SignsValue right_value = (SignsValue) right;
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
			return Signs.getInstance().create(n, z, p);
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
		if (right instanceof SignsValue) {
			SignsValue right_value = (SignsValue) right;
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
			return Signs.getInstance().create(n, z, p);
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
		if (right instanceof SignsValue) {
			SignsValue right_value = (SignsValue) right;
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
			return Signs.getInstance().create(n, z, p);
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
		if (right instanceof SignsValue) {
			SignsValue right_value = (SignsValue) right;
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
			return Signs.getInstance().create(n, z, p);
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
		// Note that x << y considers only the least five bits of y
		// Three disjoint cases:
		// * x = ZERO, y in { NEG, ZERO, POS} => x << y = ZERO
		// * x in { NEG, ZERO, POS}, y = ZERO => x << y = x
		// * x in { NEG, POS }, y in { NEG, POS } => x << y = { POS, ZERO, NEG }		
		if (right instanceof SignsValue) {
			SignsValue right_value = (SignsValue) right;
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
			return Signs.getInstance().create(n, z, p);
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
		// Note that x >> y considers only the least five bits of y, sign of x is
		// preserved
		// Four disjoint cases:
		// * x = ZERO, y in { NEG, ZERO, POS } => x >> y = ZERO
		// * x in { NEG, ZERO, POS }, y = ZERO => x >> y = x
		// * x = POS, y in { NEG, POS } => x >> y = { ZERO, POS }
		// * x = NEG, y in { NEG, POS } => x >> y = NEG		
		if (right instanceof SignsValue) {
			SignsValue right_value = (SignsValue) right;
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
			return Signs.getInstance().create(n, z, p);
		} else
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
		// Note that x >>> y considers only the least five bits of y, sign of x is
		// not preserved
		// Three disjoint cases:
		// * x = ZERO, y in { NEG, ZERO, POS } => x >> y = ZERO
		// * x in { NEG, ZERO, POS }, y = ZERO => x >> y = x
		// * x in { NEG, POS }, y in { NEG, POS } => x >> y = { ZERO, POS }		
		if (right instanceof SignsValue) {
			SignsValue right_value = (SignsValue) right;
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
			return Signs.getInstance().create(n, z, p);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _neg() {
		return Signs.getInstance().create(can_be_POS(), can_be_ZERO(), can_be_NEG());
	}

	@Override
	public AbstractBoolean _ge(AbstractValue right) {
		if (right instanceof SignsValue) {
			SignsValue right_value = (SignsValue) right;
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
		return _ge(abs.abstractMap(right));
	}

	@Override
	public AbstractBoolean _gt(AbstractValue right) {
		if (right instanceof SignsValue) {
			SignsValue right_value = (SignsValue) right;
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
		return _gt(abs.abstractMap(right));
	}

	@Override
	public AbstractBoolean _le(AbstractValue right) {
		if (right instanceof SignsValue) {
			SignsValue right_value = (SignsValue) right;
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
		return _le(abs.abstractMap(right));
	}

	@Override
	public AbstractBoolean _lt(AbstractValue right) {
		if (right instanceof SignsValue) {
			SignsValue right_value = (SignsValue) right;
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
		return _lt(abs.abstractMap(right));
	}

	public AbstractBoolean _eq(AbstractValue right) {
		if (right instanceof SignsValue) {
			SignsValue right_value = (SignsValue) right;
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
		return _eq(abs.abstractMap(right));
	}

	public AbstractBoolean _ne(AbstractValue right) {
		return _eq(right).not();
	}

	@Override
	public AbstractBoolean _ne(int right) {
		return _ne(abs.abstractMap(right));
	}

	@Override
	protected AbstractValue _minus_reverse(int right) {
		return abs.abstractMap(right)._minus(this);
	}

	@Override
	protected AbstractValue _minus_reverse(long right) {
		return abs.abstractMap(right)._minus(this);
	}

	@Override
	protected AbstractValue _minus_reverse(float right) {
		return abs.abstractMap(right)._minus(this);
	}

	@Override
	protected AbstractValue _minus_reverse(double right) {
		return abs.abstractMap(right)._minus(this);
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
	
	public String toString() {
		if (this instanceof SignsValue) {
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
			if (this == Signs.ZERO)
				return "ZERO";
			if (this == Signs.POS)
				return "POS";
			if (this == Signs.NEG)
				return "NEG";
		}
		throw new RuntimeException("## Error: unknown abstraction");
	}
}
