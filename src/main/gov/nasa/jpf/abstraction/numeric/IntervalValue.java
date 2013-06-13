package gov.nasa.jpf.abstraction.numeric;

import java.util.HashSet;
import java.util.Set;

public class IntervalValue extends AbstractValue {

	public IntervalValue(int key)
	{
		super(key);
	}

	public boolean can_be_GREATER() {
		int key = this.getKey();
		return key == 2 || key == 3 || key == 4 || key == 6;
	}

	public boolean can_be_LESS() {
		int key = this.getKey();
		return key == 0 || key == 4 || key == 5 || key == 6;
	}

	public boolean can_be_INSIDE() {
		int key = this.getKey();
		return key == 1 || key == 3 || key == 5 || key == 6;
	}
	
	@Override
	public Set<AbstractValue> getTokens() {
		Set<AbstractValue> tokens = new HashSet<AbstractValue>();
		if (can_be_LESS())
			tokens.add(((Interval)abs).createLess());
		if (can_be_INSIDE())
			tokens.add(((Interval)abs).createInside());
		if (can_be_GREATER())
			tokens.add(((Interval)abs).createGreater());
		return tokens;
	}

	// returns possible tokens (enumerated from 0) in order {NEG, ZERO, POS}
	@Override
	public AbstractValue getToken(int key) {
		int num = getTokensNumber();
		if (key < 0 || key >= num)
			throw new RuntimeException("Wrong TOP token");
		if (can_be_LESS())
			if (key == 0)
				return ((Interval)abs).createLess();
			else if (can_be_INSIDE())
				return (key == 1) ? ((Interval)abs).createInside() : ((Interval)abs).createGreater();
			else
				return ((Interval)abs).createGreater();
		else if (can_be_INSIDE())
			return (key == 0) ? ((Interval)abs).createInside() : ((Interval)abs).createGreater();
		else
			return ((Interval)abs).createGreater();
	}

	@Override
	public int getTokensNumber() {
		int result = 0;
		if (can_be_GREATER())
			++result;
		if (can_be_LESS())
			++result;
		if (can_be_INSIDE())
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
			boolean gr = can_be_INSIDE() || can_be_GREATER();
			boolean in = (can_be_INSIDE() && ((Interval)abs).MIN != ((Interval)abs).MAX) || can_be_LESS();
			boolean le = can_be_LESS();
			return ((Interval)abs).create(le, in, gr);
		} else if (right == -1) {
			boolean le = can_be_INSIDE() || can_be_LESS();
			boolean in = (can_be_INSIDE() && ((Interval)abs).MIN != ((Interval)abs).MAX)
					|| can_be_GREATER();
			boolean gr = can_be_GREATER();
			return ((Interval)abs).create(le, in, gr);
		} else
			return _plus(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _plus(long right) {
		return _plus(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _plus(float right) {
		return _plus(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _plus(double right) {
		return _plus(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _plus(AbstractValue right) {
		if (right instanceof IntervalValue) {
			IntervalValue right_value = (IntervalValue) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (this.can_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, ((Interval)abs).MIN);
			}
			if (this.can_be_INSIDE()) {
				left1 = Math.min(left1, ((Interval)abs).MIN);
				right1 = Math.max(right1, ((Interval)abs).MAX);
			}
			if (this.can_be_GREATER()) {
				left1 = Math.min(left1, ((Interval)abs).MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.can_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, ((Interval)abs).MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, ((Interval)abs).MIN);
				right2 = Math.max(right2, ((Interval)abs).MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, ((Interval)abs).MAX);
				right2 = Math.max(right2, Double.POSITIVE_INFINITY);
			}
			double res_left = left1 + left2, res_right = right1 + right2;
			return ((Interval)abs).create(res_left < ((Interval)abs).MIN, res_right >= ((Interval)abs).MIN
					&& res_left <= ((Interval)abs).MAX, res_right > ((Interval)abs).MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _minus(AbstractValue right) {
		if (right instanceof IntervalValue) {
			IntervalValue right_value = (IntervalValue) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (this.can_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, ((Interval)abs).MIN);
			}
			if (this.can_be_INSIDE()) {
				left1 = Math.min(left1, ((Interval)abs).MIN);
				right1 = Math.max(right1, ((Interval)abs).MAX);
			}
			if (this.can_be_GREATER()) {
				left1 = Math.min(left1, ((Interval)abs).MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.can_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, ((Interval)abs).MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, ((Interval)abs).MIN);
				right2 = Math.max(right2, ((Interval)abs).MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, ((Interval)abs).MAX);
				right2 = Math.max(right2, Double.POSITIVE_INFINITY);
			}
			double res_left = left1 - right2, res_right = right1 - left2;
			return ((Interval)abs).create(res_left < ((Interval)abs).MIN, res_right >= ((Interval)abs).MIN
					&& res_left <= ((Interval)abs).MAX, res_right > ((Interval)abs).MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _minus(int right) {
		return _minus(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _minus(long right) {
		return _minus(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _minus(float right) {
		return _minus(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _minus(double right) {
		return _minus(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _mul(int right) {
		return _mul(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _mul(long right) {
		return _mul(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _mul(float right) {
		return _mul(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _mul(double right) {
		return _mul(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _mul(AbstractValue right) {
		if (right instanceof IntervalValue) {
			IntervalValue right_value = (IntervalValue) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (this.can_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, ((Interval)abs).MIN);
			}
			if (this.can_be_INSIDE()) {
				left1 = Math.min(left1, ((Interval)abs).MIN);
				right1 = Math.max(right1, ((Interval)abs).MAX);
			}
			if (this.can_be_GREATER()) {
				left1 = Math.min(left1, ((Interval)abs).MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.can_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, ((Interval)abs).MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, ((Interval)abs).MIN);
				right2 = Math.max(right2, ((Interval)abs).MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, ((Interval)abs).MAX);
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
			return ((Interval)abs).create(res_left < ((Interval)abs).MIN, res_right >= ((Interval)abs).MIN
					&& res_left <= ((Interval)abs).MAX, res_right > ((Interval)abs).MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _div(int right) {
		return _div(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _div(long right) {
		return _div(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _div(float right) {
		return _div(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _div(double right) {
		return _div(((Interval)abs).abstractMap(right));
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
	public AbstractValue _div(AbstractValue right) {
		if (right instanceof IntervalValue) {
			IntervalValue right_value = (IntervalValue) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (this.can_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, ((Interval)abs).MIN);
			}
			if (this.can_be_INSIDE()) {
				left1 = Math.min(left1, ((Interval)abs).MIN);
				right1 = Math.max(right1, ((Interval)abs).MAX);
			}
			if (this.can_be_GREATER()) {
				left1 = Math.min(left1, ((Interval)abs).MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.can_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, ((Interval)abs).MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, ((Interval)abs).MIN);
				right2 = Math.max(right2, ((Interval)abs).MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, ((Interval)abs).MAX);
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
			return ((Interval)abs).create(res_left < ((Interval)abs).MIN, res_right >= ((Interval)abs).MIN
					&& res_left <= ((Interval)abs).MAX, res_right > ((Interval)abs).MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _rem(int right) {
		return _rem(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _rem(long right) {
		return _rem(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _rem(float right) {
		return _rem(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _rem(double right) {
		return _rem(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _rem(AbstractValue right) {
		if (right instanceof IntervalValue) {
			IntervalValue right_value = (IntervalValue) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (this.can_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, ((Interval)abs).MIN);
			}
			if (this.can_be_INSIDE()) {
				left1 = Math.min(left1, ((Interval)abs).MIN);
				right1 = Math.max(right1, ((Interval)abs).MAX);
			}
			if (this.can_be_GREATER()) {
				left1 = Math.min(left1, ((Interval)abs).MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.can_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, ((Interval)abs).MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, ((Interval)abs).MIN);
				right2 = Math.max(right2, ((Interval)abs).MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, ((Interval)abs).MAX);
				right2 = Math.max(right2, Double.POSITIVE_INFINITY);
			}
			if (left2 <= 0 && 0 <= right2)
				; // TODO: Handle division by zero
			double res_left = 0;
			double res_right = ____max(Math.abs(left1), Math.abs(right1),
					Math.abs(left2), Math.abs(right2));

			return ((Interval)abs).create(res_left < ((Interval)abs).MIN, res_right >= ((Interval)abs).MIN
					&& res_left <= ((Interval)abs).MAX, res_right > ((Interval)abs).MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _bitwise_and(int right) {
		return _bitwise_and(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _bitwise_and(long right) {
		return _bitwise_and(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _bitwise_and(AbstractValue right) {
		if (right instanceof IntervalValue) {
			return ((Interval)abs).create(true, true, true);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _bitwise_or(int right) {
		return _bitwise_or(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _bitwise_or(long right) {
		return _bitwise_or(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _bitwise_or(AbstractValue right) {
		if (right instanceof IntervalValue) {
			return ((Interval)abs).create(true, true, true);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _bitwise_xor(int right) {
		return _bitwise_xor(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _bitwise_xor(long right) {
		return _bitwise_xor(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _bitwise_xor(AbstractValue right) {
		if (right instanceof IntervalValue) {
			return ((Interval)abs).create(true, true, true);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _shift_left(int right) {
		return _shift_left(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _shift_left(long right) {
		return _shift_left(((Interval)abs).abstractMap(right));
	}

	// Note that x << y considers only the least five bits of y
	@Override
	public AbstractValue _shift_left(AbstractValue right) {
		if (right instanceof IntervalValue) {
			return ((Interval)abs).create(true, true, true);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _shift_right(int right) {
		return _shift_right(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _shift_right(long right) {
		return _shift_right(((Interval)abs).abstractMap(right));
	}

	// Note that x >> y considers only the least five bits of y, sign of x is
	// preserved
	@Override
	public AbstractValue _shift_right(AbstractValue right) {
		if (right instanceof IntervalValue) {
			return ((Interval)abs).create(true, true, true);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _unsigned_shift_right(int right) {
		return _unsigned_shift_right(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _unsigned_shift_right(long right) {
		return _unsigned_shift_right(((Interval)abs).abstractMap(right));
	}

	// Note that x >>> y considers only the least five bits of y, sign of x is
	// not preserved
	@Override
	public AbstractValue _unsigned_shift_right(AbstractValue right) {
		if (right instanceof IntervalValue) {
			return ((Interval)abs).create(true, true, true);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _neg() {
		double left = Double.POSITIVE_INFINITY, right = Double.NEGATIVE_INFINITY;
		if (this.can_be_LESS()) {
			left = Math.min(left, Double.NEGATIVE_INFINITY);
			right = Math.max(right, ((Interval)abs).MIN);
		}
		if (this.can_be_INSIDE()) {
			left = Math.min(left, ((Interval)abs).MIN);
			right = Math.max(right, ((Interval)abs).MAX);
		}
		if (this.can_be_GREATER()) {
			left = Math.min(left, ((Interval)abs).MAX);
			right = Math.max(right, Double.POSITIVE_INFINITY);
		}
		double res_right = -left;
		double res_left = -right;
		return ((Interval)abs).create(res_left < ((Interval)abs).MIN, res_right >= ((Interval)abs).MIN
				&& res_left <= ((Interval)abs).MAX, res_right > ((Interval)abs).MAX);
	}

	@Override
	public AbstractBoolean _ge(AbstractValue right) {
		if (right instanceof IntervalValue) {
			IntervalValue right_value = (IntervalValue) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (this.can_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, ((Interval)abs).MIN);
			}
			if (this.can_be_INSIDE()) {
				left1 = Math.min(left1, ((Interval)abs).MIN);
				right1 = Math.max(right1, ((Interval)abs).MAX);
			}
			if (this.can_be_GREATER()) {
				left1 = Math.min(left1, ((Interval)abs).MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.can_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, ((Interval)abs).MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, ((Interval)abs).MIN);
				right2 = Math.max(right2, ((Interval)abs).MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, ((Interval)abs).MAX);
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
		return _ge(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractBoolean _gt(AbstractValue right) {
		if (right instanceof IntervalValue) {
			IntervalValue right_value = (IntervalValue) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (this.can_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, ((Interval)abs).MIN);
			}
			if (this.can_be_INSIDE()) {
				left1 = Math.min(left1, ((Interval)abs).MIN);
				right1 = Math.max(right1, ((Interval)abs).MAX);
			}
			if (this.can_be_GREATER()) {
				left1 = Math.min(left1, ((Interval)abs).MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.can_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, ((Interval)abs).MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, ((Interval)abs).MIN);
				right2 = Math.max(right2, ((Interval)abs).MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, ((Interval)abs).MAX);
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
		return _gt(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractBoolean _le(AbstractValue right) {
		if (right instanceof IntervalValue) {
			IntervalValue right_value = (IntervalValue) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (this.can_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, ((Interval)abs).MIN);
			}
			if (this.can_be_INSIDE()) {
				left1 = Math.min(left1, ((Interval)abs).MIN);
				right1 = Math.max(right1, ((Interval)abs).MAX);
			}
			if (this.can_be_GREATER()) {
				left1 = Math.min(left1, ((Interval)abs).MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.can_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, ((Interval)abs).MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, ((Interval)abs).MIN);
				right2 = Math.max(right2, ((Interval)abs).MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, ((Interval)abs).MAX);
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
		return _le(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractBoolean _lt(AbstractValue right) {
		if (right instanceof IntervalValue) {
			IntervalValue right_value = (IntervalValue) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (this.can_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, ((Interval)abs).MIN);
			}
			if (this.can_be_INSIDE()) {
				left1 = Math.min(left1, ((Interval)abs).MIN);
				right1 = Math.max(right1, ((Interval)abs).MAX);
			}
			if (this.can_be_GREATER()) {
				left1 = Math.min(left1, ((Interval)abs).MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.can_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, ((Interval)abs).MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, ((Interval)abs).MIN);
				right2 = Math.max(right2, ((Interval)abs).MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, ((Interval)abs).MAX);
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
		return _lt(((Interval)abs).abstractMap(right));
	}

	public AbstractBoolean _eq(AbstractValue right) {
		if (right instanceof IntervalValue) {
			IntervalValue right_value = (IntervalValue) right;
			double left1 = Double.POSITIVE_INFINITY, right1 = Double.NEGATIVE_INFINITY;
			double left2 = Double.POSITIVE_INFINITY, right2 = Double.NEGATIVE_INFINITY;
			if (this.can_be_LESS()) {
				left1 = Math.min(left1, Double.NEGATIVE_INFINITY);
				right1 = Math.max(right1, ((Interval)abs).MIN);
			}
			if (this.can_be_INSIDE()) {
				left1 = Math.min(left1, ((Interval)abs).MIN);
				right1 = Math.max(right1, ((Interval)abs).MAX);
			}
			if (this.can_be_GREATER()) {
				left1 = Math.min(left1, ((Interval)abs).MAX);
				right1 = Math.max(right1, Double.POSITIVE_INFINITY);
			}
			if (right_value.can_be_LESS()) {
				left2 = Math.min(left2, Double.NEGATIVE_INFINITY);
				right2 = Math.max(right2, ((Interval)abs).MIN);
			}
			if (right_value.can_be_INSIDE()) {
				left2 = Math.min(left2, ((Interval)abs).MIN);
				right2 = Math.max(right2, ((Interval)abs).MAX);
			}
			if (right_value.can_be_GREATER()) {
				left2 = Math.min(left2, ((Interval)abs).MAX);
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
		return _eq(((Interval)abs).abstractMap(right));
	}

	public AbstractBoolean _ne(AbstractValue right) {
		return _eq(right).not();
	}

	@Override
	public AbstractBoolean _ne(int right) {
		return _ne(((Interval)abs).abstractMap(right));
	}

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         AbstractValue; Signs.NEG if this AbstractValue is numerically less
	 *         than the operand; and Signs.POS if this AbstractValue is
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
		return this._cmp(((Interval)abs).abstractMap(right));
	}

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         AbstractValue; Signs.NEG if this AbstractValue is numerically less
	 *         than the operand; and Signs.POS if this AbstractValue is
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
		return this._cmpg(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _cmpg(double right) {
		return this._cmpg(((Interval)abs).abstractMap(right));
	}

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         AbstractValue; Signs.NEG if this AbstractValue is numerically less
	 *         than the operand; and Signs.POS if this AbstractValue is
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
		return this._cmpl(((Interval)abs).abstractMap(right));
	}

	@Override
	public AbstractValue _cmpl(double right) {
		return this._cmpl(((Interval)abs).abstractMap(right));
	}	
	
	@Override
	protected AbstractValue _div_reverse(int right) {
		return ((Interval)abs).abstractMap(right)._div(this);
	}

	@Override
	protected AbstractValue _div_reverse(long right) {
		return ((Interval)abs).abstractMap(right)._div(this);
	}

	@Override
	protected AbstractValue _div_reverse(float right) {
		return ((Interval)abs).abstractMap(right)._div(this);
	}

	@Override
	protected AbstractValue _div_reverse(double right) {
		return ((Interval)abs).abstractMap(right)._div(this);
	}

	@Override
	protected AbstractValue _cmp_reverse(long right) {
		return ((Interval)abs).abstractMap(right)._cmp(this);
	}

	@Override
	protected AbstractValue _cmpl_reverse(float right) {
		return ((Interval)abs).abstractMap(right)._cmpl(this);
	}

	@Override
	protected AbstractValue _cmpl_reverse(double right) {
		return ((Interval)abs).abstractMap(right)._cmpl(this);
	}

	@Override
	protected AbstractValue _cmpg_reverse(float right) {
		return ((Interval)abs).abstractMap(right)._cmpg(this);
	}

	@Override
	protected AbstractValue _cmpg_reverse(double right) {
		return ((Interval)abs).abstractMap(right)._cmpg(this);
	}

	@Override
	protected AbstractValue _rem_reverse(int right) {
		return ((Interval)abs).abstractMap(right)._rem(this);
	}

	@Override
	protected AbstractValue _rem_reverse(long right) {
		return ((Interval)abs).abstractMap(right)._rem(this);
	}

	@Override
	protected AbstractValue _rem_reverse(float right) {
		return ((Interval)abs).abstractMap(right)._rem(this);
	}

	@Override
	protected AbstractValue _rem_reverse(double right) {
		return ((Interval)abs).abstractMap(right)._rem(this);
	}

	@Override
	protected AbstractValue _shift_left_reverse(int right) {
		return ((Interval)abs).abstractMap(right)._shift_left(this);
	}

	@Override
	protected AbstractValue _shift_left_reverse(long right) {
		return ((Interval)abs).abstractMap(right)._shift_left(this);
	}

	@Override
	protected AbstractValue _shift_right_reverse(int right) {
		return ((Interval)abs).abstractMap(right)._shift_right(this);
	}

	@Override
	protected AbstractValue _shift_right_reverse(long right) {
		return ((Interval)abs).abstractMap(right)._shift_right(this);
	}

	@Override
	protected AbstractValue _unsigned_shift_right_reverse(int right) {
		return ((Interval)abs).abstractMap(right)._unsigned_shift_right(this);
	}

	@Override
	protected AbstractValue _unsigned_shift_right_reverse(long right) {
		return ((Interval)abs).abstractMap(right)._unsigned_shift_right(this);
	}

	@Override
	protected AbstractBoolean _lt_reverse(int right) {
		return ((Interval)abs).abstractMap(right)._lt(this);
	}

	@Override
	protected AbstractBoolean _le_reverse(int right) {
		return ((Interval)abs).abstractMap(right)._le(this);
	}

	@Override
	protected AbstractBoolean _ge_reverse(int right) {
		return ((Interval)abs).abstractMap(right)._ge(this);
	}

	@Override
	protected AbstractBoolean _gt_reverse(int right) {
		return ((Interval)abs).abstractMap(right)._gt(this);
	}

	public String toString() {
		if (getKey() == Interval.AbstractIntervalValueType.LESS.key) {
			return "(-INF, MIN)";
		} else if (getKey() == Interval.AbstractIntervalValueType.INSIDE.key) {
			return "[MIN, MAX]";
		} else if (getKey() == Interval.AbstractIntervalValueType.GREATER.key) {
			return "(MAX, +INF)";
		} else {
			if (this.isComposite()) {
				String s = null;
				if (this.can_be_LESS())
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
			return "";
		}
	}
}
