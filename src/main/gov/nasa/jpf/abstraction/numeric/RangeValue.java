package gov.nasa.jpf.abstraction.numeric;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RangeValue extends AbstractValue {
	
	Set<Integer> values = new HashSet<Integer>();
	
	public RangeValue(int key) {
		super(key);
	}

	@Override
	public Set<AbstractValue> getTokens() {
		Set<AbstractValue> tokens = new HashSet<AbstractValue>();
		for (Integer e : values)
			tokens.add(abs.abstractMap(e));
		return tokens;		
	}	

	// returns possible tokens from TOP in order {NEG, ZERO, POS}
	@Override
	public AbstractValue getToken(int key) {
		int num = getTokensNumber();
		if (key < 0 || key >= num)
			throw new RuntimeException("Wrong TOP token");
		return abs.abstractMap((Integer)(values.toArray()[key]));
	}
	
	public int get_value() {
		return getKey()+((Range)abs).MIN-1;
	}
	
	@Override
	public int getTokensNumber() {
		return values.size();
	}
	
	/**
	 * @return true, if this abstraction is a single value from the domain;
	 * false, if this abstraction represents a set of values from the domain.
	 */	
	@Override
	public boolean isComposite() {
		return values.size() > 1;
	}
	
	private double ___min(double... args) {
		double res = args[0];
		for (int i = 1; i < args.length; ++i)
			if (args[i] < res)
					res = args[i];
		return res;
	}
	
	private double ___max(double... args) {
		double res = args[0];
		for (int i = 1; i < args.length; ++i)
			if (args[i] > res)
					res = args[i];
		return res;
	}		
	
	@Override
	public AbstractValue _bitwise_and(AbstractValue right) {
		// result is extremely difficult to predict, so this returns TOP
		if (right instanceof RangeValue) {	
			Set<Integer> values = new HashSet<Integer>();
			for (int v = ((Range)abs).MIN-1; v <= ((Range)abs).MAX+1; ++v)
				values.add(v);
			return ((Range)abs).create(values);
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
	public AbstractValue _bitwise_or(AbstractValue right) {
		// result is extremely difficult to predict, so this returns TOP	
		if (right instanceof RangeValue) {	
			Set<Integer> values = new HashSet<Integer>();
			for (int v = ((Range)abs).MIN-1; v <= ((Range)abs).MAX+1; ++v)
				values.add(v);
			return ((Range)abs).create(values);	
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
	public AbstractValue _bitwise_xor(AbstractValue right) {
		// result is extremely difficult to predict, so this returns TOP
		if (right instanceof RangeValue) {	
			Set<Integer> values = new HashSet<Integer>();
			for (int v = ((Range)abs).MIN-1; v <= ((Range)abs).MAX+1; ++v)
				values.add(v);
			return ((Range)abs).create(values);	
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
	protected AbstractValue _cmp_reverse(long right) {
		return abs.abstractMap(right)._cmp(this);
	}		
	
	@Override
	protected AbstractValue _cmpg_reverse(double right) {
		return abs.abstractMap(right)._cmpg(this);
	}

	@Override
	protected AbstractValue _cmpg_reverse(float right) {
		return abs.abstractMap(right)._cmpg(this);
	}

	@Override
	protected AbstractValue _cmpl_reverse(double right) {
		return abs.abstractMap(right)._cmpl(this);
	}
	
	@Override
	protected AbstractValue _cmpl_reverse(float right) {
		return abs.abstractMap(right)._cmpl(this);
	}

	@Override
	public AbstractValue _div(AbstractValue right) {
		if (right instanceof RangeValue) {		
			RangeValue op = (RangeValue)right;
			Set<Integer> values = new HashSet<Integer>(); // result
			Integer leftArr[] = this.values.toArray(new Integer[this.values.size()]);
			Integer rightArr[] = op.values.toArray(new Integer[op.values.size()]);

			/*
			 * the idea is to iterate through all possible pairs of operands,
			 * bound them by intervals [lv1, lv2] and [rv1, rv2] and perform
			 * the operation on inequality lv1 <= lv <= lv2 and rv1 <= rv <= rv2;
			 * then we check how the resulting [min, max] intersects [MIN, MAX]
			 */
			for (Integer lv : leftArr) {
				double lv1 = lv, lv2 = lv;
				if (lv < ((Range)abs).MIN) {
					lv1 = Double.NEGATIVE_INFINITY;
					lv2 = ((Range)abs).MIN-1;
				} else if (lv > ((Range)abs).MAX) {
					lv1 = ((Range)abs).MAX+1;
					lv2 = Double.POSITIVE_INFINITY;
				}
				for (Integer rv : rightArr) {
					double rv1 = rv, rv2 = rv;
					if (rv < ((Range)abs).MIN) {
						rv1 = Double.NEGATIVE_INFINITY;
						rv2 = ((Range)abs).MIN-1;
					} else if (lv > ((Range)abs).MAX) {
						rv1 = ((Range)abs).MAX+1;
						rv2 = Double.POSITIVE_INFINITY;
					}
					double _min, _max;
					if (rv1 < 0 && 0 < rv2)
						throw new RuntimeException("### ERROR: Division by ZERO may happen"); 
					// TODO: Handle division by zero
					if ((lv1 <= 0 && 0 <= lv2) || (rv1 < 0 && 0 < rv2)) {
						_min = ___min(lv1/rv1, lv1/rv2, lv2/rv1, lv2/rv2, 0);
						_max = ___max(lv1/rv1, lv1/rv2, lv2/rv1, lv2/rv2, 0);
					} else {
						_min = ___min(lv1/rv1, lv1/rv2, lv2/rv1, lv2/rv2);
						_max = ___max(lv1/rv1, lv1/rv2, lv2/rv1, lv2/rv2);
					}
					int min, max;
					if (_min < ((Range)abs).MIN)
						min = ((Range)abs).MIN-1;
					else if (_min > ((Range)abs).MAX)
						min = ((Range)abs).MAX+1;
					else
						min = (int)_min;
					if (_max < ((Range)abs).MIN)
						max = ((Range)abs).MIN-1;
					else if (_max > ((Range)abs).MAX)
						max = ((Range)abs).MAX+1;
					else
						max = (int)_max;					
					for (int v = min; v <= max; ++v)
						values.add(v);
				}
			}
			return ((Range)abs).create(values);
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
	protected AbstractValue _div_reverse(double right) {
		return abs.abstractMap(right)._div(this);
	}

	@Override
	protected AbstractValue _div_reverse(float right) {
		return abs.abstractMap(right)._div(this);
	}

	@Override
	protected AbstractValue _div_reverse(int right) {
		return abs.abstractMap(right)._div(this);
	}

	@Override
	protected AbstractValue _div_reverse(long right) {
		return abs.abstractMap(right)._div(this);
	}
	
	public AbstractBoolean _eq(AbstractValue right) {
		if (right instanceof RangeValue) {
			RangeValue op = (RangeValue)right;
			List<Integer> leftList = Arrays.asList(this.values.toArray(new Integer[this.values.size()]));
			Integer rightArr[] = op.values.toArray(new Integer[op.values.size()]);
			
			// check if two lists contain:
			// * for true — any pair of equal elements
			// * for false — any pair of different elements (or their sizes are different)
			boolean t = false, f = leftList.size() != rightArr.length;
			for (int r : rightArr)
				if (leftList.contains(r)) // TODO: consider using binary search
					t = true;
				else
					f = true;
			if (f & t)
				return AbstractBoolean.TOP;
			else return (f)? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	public AbstractBoolean _eq(int right) {
		return _eq(abs.abstractMap(right));
	}
	
	@Override
	public AbstractBoolean _ge(AbstractValue right) {
		if (right instanceof RangeValue) {
			RangeValue op = (RangeValue)right;
			// just check how two segments intersect
			int lMin = Collections.min(this.values); // get the least and the biggest values 
			int lMax = Collections.max(this.values);
			int rMin = Collections.min(op.values); // get the least and the biggest values 
			int rMax = Collections.max(op.values);	
			boolean t = lMax >= rMin;
			boolean f = lMin < rMax || (lMin < ((Range)abs).MIN && rMax < ((Range)abs).MIN);
			if (f & t)
				return AbstractBoolean.TOP;
			else return (f)? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}
	
	@Override
	public AbstractBoolean _ge(int right) {
		return _ge(abs.abstractMap(right));
	}

	@Override
	protected AbstractBoolean _ge_reverse(int right) {
		return abs.abstractMap(right)._ge(this);
	}

	@Override
	public AbstractBoolean _gt(AbstractValue right) {
		if (right instanceof RangeValue) {
			RangeValue op = (RangeValue)right;
			// just check how two segments intersect
			int lMin = Collections.min(this.values); // get the least and the biggest values 
			int lMax = Collections.max(this.values);
			int rMin = Collections.min(op.values); // get the least and the biggest values 
			int rMax = Collections.max(op.values);			
			boolean t = lMax > rMin || (lMax > ((Range)abs).MAX && rMin > ((Range)abs).MAX);
			boolean f = lMin <= rMax;
			if (f & t)
				return AbstractBoolean.TOP;
			else return (f)? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _gt(int right) {
		return _gt(abs.abstractMap(right));
	}

	@Override
	protected AbstractBoolean _gt_reverse(int right) {
		return abs.abstractMap(right)._gt(this);
	}

	@Override
	public AbstractBoolean _le(AbstractValue right) {
		if (right instanceof RangeValue) {
			RangeValue op = (RangeValue)right;
			// just check how two segments intersect
			int lMin = Collections.min(this.values); // get the least and the biggest values 
			int lMax = Collections.max(this.values);
			int rMin = Collections.min(op.values); // get the least and the biggest values 
			int rMax = Collections.max(op.values);		
			boolean t = lMin <= rMax;
			boolean f = lMax > rMin || (lMax > ((Range)abs).MAX && rMin > ((Range)abs).MAX);
			if (f & t)
				return AbstractBoolean.TOP;
			else return (f)? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _le(int right) {
		return _le(abs.abstractMap(right));
	}

	@Override
	protected AbstractBoolean _le_reverse(int right) {
		return abs.abstractMap(right)._le(this);
	}

	@Override
	public AbstractBoolean _lt(AbstractValue right) {
		if (right instanceof RangeValue) {
			RangeValue op = (RangeValue)right;
			// just check how two segments intersect
			int lMin = Collections.min(this.values); // get the least and the biggest values 
			int lMax = Collections.max(this.values);
			int rMin = Collections.min(op.values); // get the least and the biggest values 
			int rMax = Collections.max(op.values);	
			boolean t = lMin < rMax || (lMin < ((Range)abs).MIN && rMax < ((Range)abs).MIN);
			boolean f = lMax >= rMin;
			if (f & t)
				return AbstractBoolean.TOP;
			else return (f)? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _lt(int right) {
		return _lt(abs.abstractMap(right));
	}

	@Override
	protected AbstractBoolean _lt_reverse(int right) {
		return abs.abstractMap(right)._lt(this);
	}

	@Override
	public AbstractValue _minus(AbstractValue right) {
		if (right instanceof RangeValue) {
			RangeValue op = (RangeValue)right;
			Set<Integer> values = new HashSet<Integer>(); // result
			Integer leftArr[] = this.values.toArray(new Integer[this.values.size()]);
			Integer rightArr[] = op.values.toArray(new Integer[op.values.size()]);

			/*
			 * the idea is to iterate through all possible pairs of operands,
			 * bound them by intervals [lv1, lv2] and [rv1, rv2] and perform
			 * the operation on inequality lv1 <= lv <= lv2 and rv1 <= rv <= rv2;
			 */			
			for (Integer lv : leftArr) {
				double lv1 = lv, lv2 = lv;
				if (lv < ((Range)abs).MIN) {
					lv1 = Double.NEGATIVE_INFINITY;
					lv2 = ((Range)abs).MIN-1;
				} else if (lv > ((Range)abs).MAX) {
					lv1 = ((Range)abs).MAX+1;
					lv2 = Double.POSITIVE_INFINITY;
				}
				for (Integer rv : rightArr) {
					double rv1 = rv, rv2 = rv;
					if (rv < ((Range)abs).MIN) {
						rv1 = Double.NEGATIVE_INFINITY;
						rv2 = ((Range)abs).MIN-1;
					} else if (lv > ((Range)abs).MAX) {
						rv1 = ((Range)abs).MAX+1;
						rv2 = Double.POSITIVE_INFINITY;
					}
					double _min = lv1+rv1, _max = lv2+rv2;
					int min, max;
					if (_min < ((Range)abs).MIN)
						min = ((Range)abs).MIN-1;
					else if (_min > ((Range)abs).MAX)
						min = ((Range)abs).MAX+1;
					else
						min = (int)_min;
					if (_max < ((Range)abs).MIN)
						max = ((Range)abs).MIN-1;
					else if (_max > ((Range)abs).MAX)
						max = ((Range)abs).MAX+1;
					else
						max = (int)_max;
					for (int v = min; v <= max; ++v)
						values.add(v);
				}
			}
			return ((Range)abs).create(values);
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
	public AbstractValue _mul(AbstractValue right) {
		if (right instanceof RangeValue) {		
			RangeValue op = (RangeValue)right;
			Set<Integer> values = new HashSet<Integer>();
			Integer leftArr[] = this.values.toArray(new Integer[this.values.size()]);
			Integer rightArr[] = op.values.toArray(new Integer[op.values.size()]);
			
			/*
			 * the idea is to iterate through all possible pairs of operands,
			 * bound them by intervals [lv1, lv2] and [rv1, rv2] and perform
			 * the operation on inequality lv1 <= lv <= lv2 and rv1 <= rv <= rv2
			 */			
			for (Integer lv : leftArr) {
				double lv1 = lv, lv2 = lv;
				if (lv < ((Range)abs).MIN) {
					lv1 = Double.NEGATIVE_INFINITY;
					lv2 = ((Range)abs).MIN-1;
				} else if (lv > ((Range)abs).MAX) {
					lv1 = ((Range)abs).MAX+1;
					lv2 = Double.POSITIVE_INFINITY;
				}
				for (Integer rv : rightArr) {
					double rv1 = rv, rv2 = rv;
					if (rv < ((Range)abs).MIN) {
						rv1 = Double.NEGATIVE_INFINITY;
						rv2 = ((Range)abs).MIN-1;
					} else if (lv > ((Range)abs).MAX) {
						rv1 = ((Range)abs).MAX+1;
						rv2 = Double.POSITIVE_INFINITY;
					}
					double _min, _max;
					if ((lv1 <= 0 && 0 <= lv2) || (rv1 <= 0 && 0 <= rv2)) {
						_min = ___min(lv1*rv1, lv1*rv2, lv2*rv1, lv2*rv2, 0);
						_max = ___max(lv1*rv1, lv1*rv2, lv2*rv1, lv2*rv2, 0);
					} else {
						_min = ___min(lv1*rv1, lv1*rv2, lv2*rv1, lv2*rv2);
						_max = ___max(lv1*rv1, lv1*rv2, lv2*rv1, lv2*rv2);
					}
					int min, max;
					if (_min < ((Range)abs).MIN)
						min = ((Range)abs).MIN-1;
					else if (_min > ((Range)abs).MAX)
						min = ((Range)abs).MAX+1;
					else
						min = (int)_min;
					if (_max < ((Range)abs).MIN)
						max = ((Range)abs).MIN-1;
					else if (_max > ((Range)abs).MAX)
						max = ((Range)abs).MAX+1;
					else
						max = (int)_max;					
					for (int v = min; v <= max; ++v)
						values.add(v);
				}
			}
			return ((Range)abs).create(values);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _mul(int right) {
		return _mul(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _mul(long right) {
		return _mul(abs.abstractMap(right));
	}

	public AbstractBoolean _ne(AbstractValue right) {
		return _eq(right).not();
	}
	
	@Override
	public AbstractBoolean _ne(int right) {
		return _ne(abs.abstractMap(right));
	}	
	
	@Override
	public AbstractValue _neg() {
		Set<Integer> values = new HashSet<Integer>();
		for (Integer v : this.values) {
			if (v > ((Range)abs).MAX)
				values.add(((Range)abs).MIN-1);
			if (v < ((Range)abs).MIN)
				values.add(((Range)abs).MAX+1);			
			if (-v > ((Range)abs).MAX)
				values.add(((Range)abs).MAX+1);
			else if (-v < ((Range)abs).MIN)
				values.add(((Range)abs).MIN-1);
			else
				values.add(-v);
		}
		return ((Range)abs).create(values);
	}	
	
	@Override
	public AbstractValue _plus(AbstractValue right) {
		if (right instanceof RangeValue) {
			RangeValue op = (RangeValue)right;
			Set<Integer> values = new HashSet<Integer>(); //result
			Integer leftArr[] = this.values.toArray(new Integer[this.values.size()]);
			Integer rightArr[] = op.values.toArray(new Integer[op.values.size()]);
			
			/*
			 * the idea is to iterate through all possible pairs of operands,
			 * bound them by intervals [lv1, lv2] and [rv1, rv2] and perform
			 * the operation on inequality lv1 <= lv <= lv2 and rv1 <= rv <= rv2
			 */						
			for (Integer lv : leftArr) {
				double lv1 = lv, lv2 = lv;
				if (lv < ((Range)abs).MIN) {
					lv1 = Double.NEGATIVE_INFINITY;
					lv2 = ((Range)abs).MIN-1;
				} else if (lv > ((Range)abs).MAX) {
					lv1 = ((Range)abs).MAX+1;
					lv2 = Double.POSITIVE_INFINITY;
				}
				for (Integer rv : rightArr) {
					double rv1 = rv, rv2 = rv;
					if (rv < ((Range)abs).MIN) {
						rv1 = Double.NEGATIVE_INFINITY;
						rv2 = ((Range)abs).MIN-1;
					} else if (lv > ((Range)abs).MAX) {
						rv1 = ((Range)abs).MAX+1;
						rv2 = Double.POSITIVE_INFINITY;
					}
					double _min = lv1+rv1, _max = lv2+rv2;
					int min, max;
					if (_min < ((Range)abs).MIN)
						min = ((Range)abs).MIN-1;
					else if (_min > ((Range)abs).MAX)
						min = ((Range)abs).MAX+1;
					else
						min = (int)_min;
					if (_max < ((Range)abs).MIN)
						max = ((Range)abs).MIN-1;
					else if (_max > ((Range)abs).MAX)
						max = ((Range)abs).MAX+1;
					else
						max = (int)_max;
					for (int v = min; v <= max; ++v)
						values.add(v);
				}
			}
			return ((Range)abs).create(values);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractValue _plus(int right) {
		if (right == 1) { // increment
			Set<Integer> result = new HashSet<Integer>();
			Integer values[] = this.values.toArray(new Integer[this.values.size()]);
			for (Integer v : values)
				if (v < ((Range)abs).MIN) {
					result.add(v);
					result.add(((Range)abs).MIN);
				} else if (v >= ((Range)abs).MAX)
					result.add(((Range)abs).MAX);
				else
					result.add(v+1);
			return ((Range)abs).create(result);
		} else if (right == -1) { //decrement
			Set<Integer> result = new HashSet<Integer>();
			Integer values[] = this.values.toArray(new Integer[this.values.size()]);
			for (Integer v : values)
				if (v > ((Range)abs).MAX) {
					result.add(v);
					result.add(((Range)abs).MAX);
				} else if (v <= ((Range)abs).MIN)
					result.add(((Range)abs).MIN);
				else
					result.add(v-1);
			return ((Range)abs).create(result);
		} else
			return _plus(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _rem(AbstractValue right) {
		if (right instanceof RangeValue) {		
			RangeValue op = (RangeValue)right;
			Set<Integer> values = new HashSet<Integer>();
			Integer leftArr[] = this.values.toArray(new Integer[this.values.size()]);
			Integer rightArr[] = op.values.toArray(new Integer[op.values.size()]);
			
			/*
			 * the idea is to iterate through all possible pairs of operands,
			 * bound them by intervals [lv1, lv2] and [rv1, rv2] and perform
			 * the operation on inequality lv1 <= lv <= lv2 and rv1 <= rv <= rv2
			 */						
			for (Integer lv : leftArr) {
				for (Integer rv : rightArr) {
					if (rv == 1) {
						values.add(0);
						continue;
					}
					int min = 0, max = lv;
					if (lv < ((Range)abs).MIN || lv > ((Range)abs).MAX)
						if (rv < ((Range)abs).MIN || rv > ((Range)abs).MAX)
							max = ((Range)abs).MAX+1;
						else
							max = Math.min(((Range)abs).MAX+1, Math.abs(rv)-1);
					else
						if (rv < ((Range)abs).MIN || rv > ((Range)abs).MAX)
							min = Math.abs(rv);
						else
							min = max = lv % rv;
					if (min < ((Range)abs).MIN)
						min = ((Range)abs).MIN-1;
					else if (min > ((Range)abs).MAX)
						min = ((Range)abs).MAX+1;
					if (max < ((Range)abs).MIN)
						max = ((Range)abs).MIN-1;
					else if (max > ((Range)abs).MAX)
						max = ((Range)abs).MAX+1;					
					for (int v = min; v <= max; ++v)
						values.add(v);
				}
			}
			return ((Range)abs).create(values);
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
	protected AbstractValue _rem_reverse(double right) {
		return abs.abstractMap(right)._rem(this);
	}

	@Override
	protected AbstractValue _rem_reverse(float right) {
		return abs.abstractMap(right)._rem(this);
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
	public AbstractValue _shift_left(AbstractValue right) {
		// result is extremely difficult to predict, so this returns TOP
		if (right instanceof RangeValue) {	
			Set<Integer> values = new HashSet<Integer>();
			for (int v = ((Range)abs).MIN-1; v <= ((Range)abs).MAX+1; ++v)
				values.add(v);
			return ((Range)abs).create(values);	
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
	protected AbstractValue _shift_left_reverse(int right) {
		return abs.abstractMap(right)._shift_left(this);
	}

	@Override
	protected AbstractValue _shift_left_reverse(long right) {
		return abs.abstractMap(right)._shift_left(this);
	}

	@Override
	public AbstractValue _shift_right(AbstractValue right) {
		// result is extremely difficult to predict, so this returns TOP
		if (right instanceof RangeValue) {	
			Set<Integer> values = new HashSet<Integer>();
			for (int v = ((Range)abs).MIN-1; v <= ((Range)abs).MAX+1; ++v)
				values.add(v);
			return ((Range)abs).create(values);	
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
	protected AbstractValue _shift_right_reverse(int right) {
		return abs.abstractMap(right)._shift_right(this);
	}

	@Override
	protected AbstractValue _shift_right_reverse(long right) {
		return abs.abstractMap(right)._shift_right(this);
	}

	@Override
	public AbstractValue _unsigned_shift_right(AbstractValue right) {
		// result is extremely difficult to predict, so this returns TOP
		if (right instanceof RangeValue) {	
			Set<Integer> values = new HashSet<Integer>();
			for (int v = ((Range)abs).MIN-1; v <= ((Range)abs).MAX+1; ++v)
				values.add(v);
			return ((Range)abs).create(values);
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
	protected AbstractValue _unsigned_shift_right_reverse(int right) {
		return abs.abstractMap(right)._unsigned_shift_right(this);
	}

	@Override
	protected AbstractValue _unsigned_shift_right_reverse(long right) {
		return abs.abstractMap(right)._unsigned_shift_right(this);
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
		return this._cmp(abs.abstractMap(right));
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
		return this._cmpg(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _cmpg(double right) {
		return this._cmpg(abs.abstractMap(right));
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
		return this._cmpl(abs.abstractMap(right));
	}

	@Override
	public AbstractValue _cmpl(double right) {
		return this._cmpl(abs.abstractMap(right));
	}	

	public String toString() {
		if (isComposite()) {
			String res = "";
			for (Integer abs : values)
				res += " or " + abs;
			return res;
		} else if (get_value() < ((Range)abs).MIN)
			return String.format("(-INF, %d)", ((Range)abs).MIN);
		else if (get_value() > ((Range)abs).MAX)
			return String.format("(%d, +INF)", ((Range)abs).MAX);
		else
			return Integer.toString(get_value());
	}

}
