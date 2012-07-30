package gov.nasa.jpf.abstraction.numeric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConcreteInterval extends Abstraction implements Comparable<ConcreteInterval> {
	
	private int MIN = 0;
	private int MAX = 0;
	
	// key is enumeration of { LESS, MIN, MIN+1, ..., MAX-1, MAX, GREATER }
	// e.g LESS.get_key() == 0, MIN.get_key() == 1, ..., MAX.get_key() == MAX-MIN+1, ...
	
	Set<Integer> values = new HashSet<Integer>();
	//Map<Integer, Abstraction> tokens_map = new HashMap<Integer, Abstraction>();
	
	@Override
	public Set<Abstraction> get_tokens() {
		Set<Abstraction> tokens = new HashSet<Abstraction>();
		for (Integer e : values)
			tokens.add(abstract_map(e));
		return tokens;		
	}	

	// returns possible tokens from TOP in order {NEG, ZERO, POS}
	@Override
	public Abstraction get_token(int key) {
		int num = get_num_tokens();
		if (key < 0 || key >= num)
			throw new RuntimeException("Wrong TOP token");
		return abstract_map((Integer)(values.toArray()[key]));
	}
	
	public int get_value() {
		return key+MIN-1;
	}
	
	@Override
	public int get_num_tokens() {
		return values.size();
	}

	public static ConcreteInterval create(int min, int max) {
		return new ConcreteInterval(-1, min, max);
	}	
	
	private ConcreteInterval(int key, int min, int max) {
		this(key);
		MIN = min;
		MAX = max;
	}
	
	private ConcreteInterval(int key) {
		set_key(key);
	}
	
	private ConcreteInterval(Set<Integer> values, int min, int max) {
		this(-1, min, max);
		if (values.size() == 0)
			throw new RuntimeException("Invalid value");		
		isTop = values.size() > 1;
		if (!isTop)
			for (Integer v : values)
				set_key(v-MIN+1);
		for (Integer v : values)
			this.values.add(v);
	}		

	@Override
	public ConcreteInterval abstract_map(int v) {
		if (v < MIN)
			v = MIN-1;
		else if (v > MAX)
			v = MAX+1;
		ConcreteInterval res = new ConcreteInterval(v-MIN+1, MIN, MAX);
		res.values.add(v);
		return res;
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
	public Abstraction _plus(int right) {
		return _plus(abstract_map(right));
	}
	@Override
	public Abstraction _plus(Abstraction right) {
		if (right instanceof ConcreteInterval) {
			ConcreteInterval op = (ConcreteInterval)right;
			Set<Integer> values = new HashSet<Integer>();
			Integer leftArr[] = this.values.toArray(new Integer[this.values.size()]);
			Integer rightArr[] = op.values.toArray(new Integer[op.values.size()]);
			for (Integer lv : leftArr) {
				double lv1 = lv, lv2 = lv;
				if (lv < MIN) {
					lv1 = Double.NEGATIVE_INFINITY;
					lv2 = MIN-1;
				} else if (lv > MAX) {
					lv1 = MAX+1;
					lv2 = Double.POSITIVE_INFINITY;
				}
				for (Integer rv : rightArr) {
					double rv1 = rv, rv2 = rv;
					if (rv < MIN) {
						rv1 = Double.NEGATIVE_INFINITY;
						rv2 = MIN-1;
					} else if (lv > MAX) {
						rv1 = MAX+1;
						rv2 = Double.POSITIVE_INFINITY;
					}
					double _min = lv1+rv1, _max = lv2+rv2;
					int min, max;
					if (_min < MIN)
						min = MIN-1;
					else if (_min > MAX)
						min = MAX+1;
					else
						min = (int)_min;
					if (_max < MIN)
						max = MIN-1;
					else if (_max > MAX)
						max = MAX+1;
					else
						max = (int)_max;
					for (int v = min; v <= max; ++v)
						values.add(v);
				}
			}
			return new ConcreteInterval(values, MIN, MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _minus(Abstraction right) {
		if (right instanceof ConcreteInterval) {
			ConcreteInterval op = (ConcreteInterval)right;
			Set<Integer> values = new HashSet<Integer>();
			Integer leftArr[] = this.values.toArray(new Integer[this.values.size()]);
			Integer rightArr[] = op.values.toArray(new Integer[op.values.size()]);
			for (Integer lv : leftArr) {
				double lv1 = lv, lv2 = lv;
				if (lv < MIN) {
					lv1 = Double.NEGATIVE_INFINITY;
					lv2 = MIN-1;
				} else if (lv > MAX) {
					lv1 = MAX+1;
					lv2 = Double.POSITIVE_INFINITY;
				}
				for (Integer rv : rightArr) {
					double rv1 = rv, rv2 = rv;
					if (rv < MIN) {
						rv1 = Double.NEGATIVE_INFINITY;
						rv2 = MIN-1;
					} else if (lv > MAX) {
						rv1 = MAX+1;
						rv2 = Double.POSITIVE_INFINITY;
					}
					double _min = lv1+rv1, _max = lv2+rv2;
					int min, max;
					if (_min < MIN)
						min = MIN-1;
					else if (_min > MAX)
						min = MAX+1;
					else
						min = (int)_min;
					if (_max < MIN)
						max = MIN-1;
					else if (_max > MAX)
						max = MAX+1;
					else
						max = (int)_max;
					for (int v = min; v <= max; ++v)
						values.add(v);
				}
			}
			return new ConcreteInterval(values, MIN, MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _minus(int right) {
		return _minus(abstract_map(right));
	}

	@Override
	public Abstraction _minus(long right) {
		return _minus(abstract_map(right));
	}

	@Override
	public Abstraction _minus_reverse(int right) {
		return abstract_map(right)._minus(this);
	}

	@Override
	public Abstraction _minus_reverse(long right) {
		return abstract_map(right)._minus(this);
	}

	@Override
	public Abstraction _mul(int right) {
		return _mul(abstract_map(right));
	}

	@Override
	public Abstraction _mul(long right) {
		return _mul(abstract_map(right));
	}

	@Override
	public Abstraction _mul(Abstraction right) {
		if (right instanceof ConcreteInterval) {		
			ConcreteInterval op = (ConcreteInterval)right;
			Set<Integer> values = new HashSet<Integer>();
			Integer leftArr[] = this.values.toArray(new Integer[this.values.size()]);
			Integer rightArr[] = op.values.toArray(new Integer[op.values.size()]);
			for (Integer lv : leftArr) {
				double lv1 = lv, lv2 = lv;
				if (lv < MIN) {
					lv1 = Double.NEGATIVE_INFINITY;
					lv2 = MIN-1;
				} else if (lv > MAX) {
					lv1 = MAX+1;
					lv2 = Double.POSITIVE_INFINITY;
				}
				for (Integer rv : rightArr) {
					double rv1 = rv, rv2 = rv;
					if (rv < MIN) {
						rv1 = Double.NEGATIVE_INFINITY;
						rv2 = MIN-1;
					} else if (lv > MAX) {
						rv1 = MAX+1;
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
					if (_min < MIN)
						min = MIN-1;
					else if (_min > MAX)
						min = MAX+1;
					else
						min = (int)_min;
					if (_max < MIN)
						max = MIN-1;
					else if (_max > MAX)
						max = MAX+1;
					else
						max = (int)_max;					
					for (int v = min; v <= max; ++v)
						values.add(v);
				}
			}
			return new ConcreteInterval(values, MIN, MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _div(int right) {
		return _div(abstract_map(right));
	}

	@Override
	public Abstraction _div(long right) {
		return _div(abstract_map(right));
	}
	
	@Override
	public Abstraction _div(Abstraction right) {
		if (right instanceof ConcreteInterval) {		
			ConcreteInterval op = (ConcreteInterval)right;
			Set<Integer> values = new HashSet<Integer>();
			Integer leftArr[] = this.values.toArray(new Integer[this.values.size()]);
			Integer rightArr[] = op.values.toArray(new Integer[op.values.size()]);
			for (Integer lv : leftArr) {
				double lv1 = lv, lv2 = lv;
				if (lv < MIN) {
					lv1 = Double.NEGATIVE_INFINITY;
					lv2 = MIN-1;
				} else if (lv > MAX) {
					lv1 = MAX+1;
					lv2 = Double.POSITIVE_INFINITY;
				}
				for (Integer rv : rightArr) {
					double rv1 = rv, rv2 = rv;
					if (rv < MIN) {
						rv1 = Double.NEGATIVE_INFINITY;
						rv2 = MIN-1;
					} else if (lv > MAX) {
						rv1 = MAX+1;
						rv2 = Double.POSITIVE_INFINITY;
					}
					double _min, _max;
					if (rv1 < 0 && 0 < rv2)
						System.out.println("### WARNING: Division by ZERO may happen"); 
					// TODO: Handle division by zero
					if ((lv1 <= 0 && 0 <= lv2) || (rv1 < 0 && 0 < rv2)) {
						_min = ___min(lv1/rv1, lv1/rv2, lv2/rv1, lv2/rv2, 0);
						_max = ___max(lv1/rv1, lv1/rv2, lv2/rv1, lv2/rv2, 0);
					} else {
						_min = ___min(lv1/rv1, lv1/rv2, lv2/rv1, lv2/rv2);
						_max = ___max(lv1/rv1, lv1/rv2, lv2/rv1, lv2/rv2);
					}
					int min, max;
					if (_min < MIN)
						min = MIN-1;
					else if (_min > MAX)
						min = MAX+1;
					else
						min = (int)_min;
					if (_max < MIN)
						max = MIN-1;
					else if (_max > MAX)
						max = MAX+1;
					else
						max = (int)_max;					
					for (int v = min; v <= max; ++v)
						values.add(v);
				}
			}
			return new ConcreteInterval(values, MIN, MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _rem(int right) {
		return _rem(abstract_map(right));
	}

	@Override
	public Abstraction _rem(long right) {
		return _rem(abstract_map(right));
	}

	@Override
	public Abstraction _rem(Abstraction right) {
		if (right instanceof ConcreteInterval) {		
			ConcreteInterval op = (ConcreteInterval)right;
			Set<Integer> values = new HashSet<Integer>();
			Integer leftArr[] = this.values.toArray(new Integer[this.values.size()]);
			Integer rightArr[] = op.values.toArray(new Integer[op.values.size()]);
			for (Integer lv : leftArr) {
				for (Integer rv : rightArr) {
					if (rv == 1) {
						values.add(0);
						continue;
					}
					int min = 0, max = lv;
					if (lv < MIN || lv > MAX)
						if (rv < MIN || rv > MAX)
							max = MAX+1;
						else
							max = Math.min(MAX+1, Math.abs(rv)-1);
					else
						if (rv < MIN || rv > MAX)
							min = Math.abs(rv);
						else
							min = max = lv % rv;
					if (min < MIN)
						min = MIN-1;
					else if (min > MAX)
						min = MAX+1;
					if (max < MIN)
						max = MIN-1;
					else if (max > MAX)
						max = MAX+1;					
					for (int v = min; v <= max; ++v)
						values.add(v);
				}
			}
			return new ConcreteInterval(values, MIN, MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _bitwise_and(int right) {
		return _bitwise_and(abstract_map(right));
	}

	@Override
	public Abstraction _bitwise_and(long right) {
		return _bitwise_and(abstract_map(right));
	}

	@Override
	public Abstraction _bitwise_and(Abstraction right) {
		if (right instanceof ConcreteInterval) {	
			Set<Integer> values = new HashSet<Integer>();
			for (int v = MIN-1; v <= MAX+1; ++v)
				values.add(v);
			return new ConcreteInterval(values, MIN, MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _bitwise_or(int right) {
		return _bitwise_or(abstract_map(right));
	}

	@Override
	public Abstraction _bitwise_or(long right) {
		return _bitwise_or(abstract_map(right));
	}

	@Override
	public Abstraction _bitwise_or(Abstraction right) {
		if (right instanceof ConcreteInterval) {	
			Set<Integer> values = new HashSet<Integer>();
			for (int v = MIN-1; v <= MAX+1; ++v)
				values.add(v);
			return new ConcreteInterval(values, MIN, MAX);	
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _bitwise_xor(int right) {
		return _bitwise_xor(abstract_map(right));
	}
	
	@Override
	public Abstraction _bitwise_xor(long right) {
		return _bitwise_xor(abstract_map(right));
	}

	@Override
	public Abstraction _bitwise_xor(Abstraction right) {
		if (right instanceof ConcreteInterval) {	
			Set<Integer> values = new HashSet<Integer>();
			for (int v = MIN-1; v <= MAX+1; ++v)
				values.add(v);
			return new ConcreteInterval(values, MIN, MAX);	
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _shift_left(int right) {
		return _shift_left(abstract_map(right));
	}

	@Override
	public Abstraction _shift_left(long right) {
		return _shift_left(abstract_map(right));
	}

	// Note that x << y considers only the least five bits of y
	@Override
	public Abstraction _shift_left(Abstraction right) {
		if (right instanceof ConcreteInterval) {	
			Set<Integer> values = new HashSet<Integer>();
			for (int v = MIN-1; v <= MAX+1; ++v)
				values.add(v);
			return new ConcreteInterval(values, MIN, MAX);	
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _shift_right(int right) {
		return _shift_right(abstract_map(right));
	}

	@Override
	public Abstraction _shift_right(long right) {
		return _shift_right(abstract_map(right));
	}

	// Note that x >> y considers only the least five bits of y, sign of x is preserved
	@Override
	public Abstraction _shift_right(Abstraction right) {
		if (right instanceof ConcreteInterval) {	
			Set<Integer> values = new HashSet<Integer>();
			for (int v = MIN-1; v <= MAX+1; ++v)
				values.add(v);
			return new ConcreteInterval(values, MIN, MAX);	
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _unsigned_shift_right(int right) {
		return _unsigned_shift_right(abstract_map(right));
	}

	@Override
	public Abstraction _unsigned_shift_right(long right) {
		return _unsigned_shift_right(abstract_map(right));
	}

	// Note that x >>> y considers only the least five bits of y, sign of x is not preserved
	@Override
	public Abstraction _unsigned_shift_right(Abstraction right) {
		if (right instanceof ConcreteInterval) {	
			Set<Integer> values = new HashSet<Integer>();
			for (int v = MIN-1; v <= MAX+1; ++v)
				values.add(v);
			return new ConcreteInterval(values, MIN, MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _neg() {
		Set<Integer> values = new HashSet<Integer>();
		for (Integer v : this.values) {
			if (v > MAX)
				values.add(MIN-1);
			if (v < MIN)
				values.add(MAX+1);			
			if (-v > MAX)
				values.add(MAX+1);
			else if (-v < MIN)
				values.add(MIN-1);
			else
				values.add(-v);
		}
		return new ConcreteInterval(values, MIN, MAX);
	}

	@Override
	public AbstractBoolean _ge(Abstraction right) {
		if (right instanceof ConcreteInterval) {
			ConcreteInterval op = (ConcreteInterval)right;
			Integer leftArr[] = this.values.toArray(new Integer[this.values.size()]);
			Integer rightArr[] = op.values.toArray(new Integer[op.values.size()]);
			int lMin = leftArr[0];
			int lMax = leftArr[leftArr.length-1];
			int rMin = rightArr[0];
			int rMax = rightArr[rightArr.length-1];			
			boolean t = lMax >= rMin;
			boolean f = lMin < rMax || (lMin < MIN && rMax < MIN);
			if (f & t)
				return AbstractBoolean.TOP;
			else return (f)? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _ge(int right) {
		return _ge(abstract_map(right));
	}

	@Override
	public AbstractBoolean _gt(Abstraction right) {
		if (right instanceof ConcreteInterval) {
			ConcreteInterval op = (ConcreteInterval)right;
			Integer leftArr[] = this.values.toArray(new Integer[this.values.size()]);
			Integer rightArr[] = op.values.toArray(new Integer[op.values.size()]);
			int lMin = leftArr[0];
			int lMax = leftArr[leftArr.length-1];
			int rMin = rightArr[0];
			int rMax = rightArr[rightArr.length-1];			
			boolean t = lMax > rMin || (lMax > MAX && rMin > MAX);
			boolean f = lMin <= rMax;
			if (f & t)
				return AbstractBoolean.TOP;
			else return (f)? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _gt(int right) {
		return _gt(abstract_map(right));
	}

	@Override
	public AbstractBoolean _le(Abstraction right) {
		if (right instanceof ConcreteInterval) {
			ConcreteInterval op = (ConcreteInterval)right;
			Integer leftArr[] = this.values.toArray(new Integer[this.values.size()]);
			Integer rightArr[] = op.values.toArray(new Integer[op.values.size()]);
			int lMin = leftArr[0];
			int lMax = leftArr[leftArr.length-1];
			int rMin = rightArr[0];
			int rMax = rightArr[rightArr.length-1];			
			boolean t = lMin <= rMax;
			boolean f = lMax > rMin || (lMax > MAX && rMin > MAX);
			if (f & t)
				return AbstractBoolean.TOP;
			else return (f)? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _le(int right) {
		return _le(abstract_map(right));
	}

	@Override
	public AbstractBoolean _lt(Abstraction right) {
		if (right instanceof ConcreteInterval) {
			ConcreteInterval op = (ConcreteInterval)right;
			Integer leftArr[] = this.values.toArray(new Integer[this.values.size()]);
			Integer rightArr[] = op.values.toArray(new Integer[op.values.size()]);
			int lMin = leftArr[0];
			int lMax = leftArr[leftArr.length-1];
			int rMin = rightArr[0];
			int rMax = rightArr[rightArr.length-1];			
			boolean t = lMin < rMax || (lMin < MIN && rMax < MIN);
			boolean f = lMax >= rMin;
			if (f & t)
				return AbstractBoolean.TOP;
			else return (f)? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _lt(int right) {
		return _lt(abstract_map(right));
	}

	public String toString() {
		if (isTop()) {
			String res = "";
			for (Integer abs : values)
				res += " or " + abs;
			return res;
		} else if (get_value() < MIN)
			return String.format("(-INF, %d)", MIN);
		else if (get_value() > MAX)
			return String.format("(%d, +INF)", MAX);
		else
			return Integer.toString(get_value());
	}

	@Override
	public int compareTo(ConcreteInterval o) {
		return Integer.valueOf(get_key()).compareTo(o.get_key());
	}
	
}
