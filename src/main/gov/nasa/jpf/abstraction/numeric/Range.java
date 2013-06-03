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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The abstract domain for given two integer or floating-point values MIN and
 * MAX is the set of all integers in interval [MIN, MAX], and also LESS and GREATER
 * to express the fact that a value is less than MIN or greater than MAX,
 * respectively. This abstraction can be used for integer values only.
 * 
 * When the result of an abstract operation cannot be defined unambiguously
 * (e.g. LESS + LESS can be LESS, GREATER or any value between them), special
 * "composite tokens" which represent a set of abstract values are returned. They
 * are not represented by static members since their number increases exponentially
 * with value of (MAX-MIN).
 * 
 * Remember, that this abstraction does not handle such floating-point values as
 * NaN and INF.
 */
public class Range extends Abstraction {
	
	// the key for state matching is enumeration of { LESS, MIN, MIN+1, ..., MAX-1, MAX, GREATER }
	// e.g LESS.get_key() == 0, MIN.get_key() == 1, ..., MAX.get_key() == MAX-MIN+1, ...
	
	// since a composite abstract value will never be the result of any bytecode
	// and thus will never take part in the state matching, all such values
	// have -1 as their key.
	
	private int MIN = 0;
	private int MAX = 0;
	
	Set<Integer> values = new HashSet<Integer>();
	//Map<Integer, Abstraction> tokens_map = new HashMap<Integer, Abstraction>();
	
	@Override
	public Set<Abstraction> getTokens() {
		Set<Abstraction> tokens = new HashSet<Abstraction>();
		for (Integer e : values)
			tokens.add(abstractMap(e));
		return tokens;		
	}	

	// returns possible tokens from TOP in order {NEG, ZERO, POS}
	@Override
	public Abstraction getToken(int key) {
		int num = getTokensNumber();
		if (key < 0 || key >= num)
			throw new RuntimeException("Wrong TOP token");
		return abstractMap((Integer)(values.toArray()[key]));
	}
	
	public int get_value() {
		return getKey()+MIN-1;
	}
	
	@Override
	public int getTokensNumber() {
		return values.size();
	}

	public static Range create(int min, int max) {
		return new Range(-1, min, max);
	}	
	
	private Range(int key, int min, int max) {
		this(key);
		MIN = min;
		MAX = max;
	}
	
	private Range(int key) {
		super(key);
	}
	
	private Range(Set<Integer> values, int min, int max) {
		this(-1, min, max);
		if (values.size() == 0)
			throw new RuntimeException("Invalid value");		
		if (values.size() > 1) // isComposite
			setKey(-1);
		else
			for (Integer v : values)
				setKey(v-MIN+1);
		for (Integer v : values)
			this.values.add(v);
	}		

	/**
	 * 
	 * @return The number of abstract values in the domain.
	 */
	public int getDomainSize() {
		return MAX-MIN+3;
	}		
	
	/**
	 * @return true, if this abstraction is a single value from the domain;
	 * false, if this abstraction represents a set of values from the domain.
	 */	
	@Override
	public boolean isComposite() {
		return values.size() > 1;
	}	
	
	@Override
	public Range abstractMap(int v) {
		if (v < MIN)
			v = MIN-1;
		else if (v > MAX)
			v = MAX+1;
		Range res = new Range(v-MIN+1, MIN, MAX);
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
	public Abstraction _bitwise_and(Abstraction right) {
		// result is extremely difficult to predict, so this returns TOP
		if (right instanceof Range) {	
			Set<Integer> values = new HashSet<Integer>();
			for (int v = MIN-1; v <= MAX+1; ++v)
				values.add(v);
			return new Range(values, MIN, MAX);
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
	public Abstraction _bitwise_or(Abstraction right) {
		// result is extremely difficult to predict, so this returns TOP	
		if (right instanceof Range) {	
			Set<Integer> values = new HashSet<Integer>();
			for (int v = MIN-1; v <= MAX+1; ++v)
				values.add(v);
			return new Range(values, MIN, MAX);	
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
	public Abstraction _bitwise_xor(Abstraction right) {
		// result is extremely difficult to predict, so this returns TOP
		if (right instanceof Range) {	
			Set<Integer> values = new HashSet<Integer>();
			for (int v = MIN-1; v <= MAX+1; ++v)
				values.add(v);
			return new Range(values, MIN, MAX);	
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
	protected Abstraction _cmp_reverse(long right) {
		return abstractMap(right)._cmp(this);
	}		
	
	@Override
	protected Abstraction _cmpg_reverse(double right) {
		return abstractMap(right)._cmpg(this);
	}

	@Override
	protected Abstraction _cmpg_reverse(float right) {
		return abstractMap(right)._cmpg(this);
	}

	@Override
	protected Abstraction _cmpl_reverse(double right) {
		return abstractMap(right)._cmpl(this);
	}
	
	@Override
	protected Abstraction _cmpl_reverse(float right) {
		return abstractMap(right)._cmpl(this);
	}

	@Override
	public Abstraction _div(Abstraction right) {
		if (right instanceof Range) {		
			Range op = (Range)right;
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
			return new Range(values, MIN, MAX);
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
	protected Abstraction _div_reverse(double right) {
		return abstractMap(right)._div(this);
	}

	@Override
	protected Abstraction _div_reverse(float right) {
		return abstractMap(right)._div(this);
	}

	@Override
	protected Abstraction _div_reverse(int right) {
		return abstractMap(right)._div(this);
	}

	@Override
	protected Abstraction _div_reverse(long right) {
		return abstractMap(right)._div(this);
	}
	
	public AbstractBoolean _eq(Abstraction right) {
		if (right instanceof Range) {
			Range op = (Range)right;
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
		return _eq(abstractMap(right));
	}
	
	@Override
	public AbstractBoolean _ge(Abstraction right) {
		if (right instanceof Range) {
			Range op = (Range)right;
			// just check how two segments intersect
			int lMin = Collections.min(this.values); // get the least and the biggest values 
			int lMax = Collections.max(this.values);
			int rMin = Collections.min(op.values); // get the least and the biggest values 
			int rMax = Collections.max(op.values);	
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
		return _ge(abstractMap(right));
	}

	@Override
	protected AbstractBoolean _ge_reverse(int right) {
		return abstractMap(right)._ge(this);
	}

	@Override
	public AbstractBoolean _gt(Abstraction right) {
		if (right instanceof Range) {
			Range op = (Range)right;
			// just check how two segments intersect
			int lMin = Collections.min(this.values); // get the least and the biggest values 
			int lMax = Collections.max(this.values);
			int rMin = Collections.min(op.values); // get the least and the biggest values 
			int rMax = Collections.max(op.values);			
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
		return _gt(abstractMap(right));
	}

	@Override
	protected AbstractBoolean _gt_reverse(int right) {
		return abstractMap(right)._gt(this);
	}

	@Override
	public AbstractBoolean _le(Abstraction right) {
		if (right instanceof Range) {
			Range op = (Range)right;
			// just check how two segments intersect
			int lMin = Collections.min(this.values); // get the least and the biggest values 
			int lMax = Collections.max(this.values);
			int rMin = Collections.min(op.values); // get the least and the biggest values 
			int rMax = Collections.max(op.values);		
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
		return _le(abstractMap(right));
	}

	@Override
	protected AbstractBoolean _le_reverse(int right) {
		return abstractMap(right)._le(this);
	}

	@Override
	public AbstractBoolean _lt(Abstraction right) {
		if (right instanceof Range) {
			Range op = (Range)right;
			// just check how two segments intersect
			int lMin = Collections.min(this.values); // get the least and the biggest values 
			int lMax = Collections.max(this.values);
			int rMin = Collections.min(op.values); // get the least and the biggest values 
			int rMax = Collections.max(op.values);	
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
		return _lt(abstractMap(right));
	}

	@Override
	protected AbstractBoolean _lt_reverse(int right) {
		return abstractMap(right)._lt(this);
	}

	@Override
	public Abstraction _minus(Abstraction right) {
		if (right instanceof Range) {
			Range op = (Range)right;
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
			return new Range(values, MIN, MAX);
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
	public Abstraction _mul(Abstraction right) {
		if (right instanceof Range) {		
			Range op = (Range)right;
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
			return new Range(values, MIN, MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _mul(int right) {
		return _mul(abstractMap(right));
	}

	@Override
	public Abstraction _mul(long right) {
		return _mul(abstractMap(right));
	}

	public AbstractBoolean _ne(Abstraction right) {
		return _eq(right).not();
	}
	
	@Override
	public AbstractBoolean _ne(int right) {
		return _ne(abstractMap(right));
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
		return new Range(values, MIN, MAX);
	}	
	
	@Override
	public Abstraction _plus(Abstraction right) {
		if (right instanceof Range) {
			Range op = (Range)right;
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
			return new Range(values, MIN, MAX);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _plus(int right) {
		if (right == 1) { // increment
			Set<Integer> result = new HashSet<Integer>();
			Integer values[] = this.values.toArray(new Integer[this.values.size()]);
			for (Integer v : values)
				if (v < MIN) {
					result.add(v);
					result.add(MIN);
				} else if (v >= MAX)
					result.add(MAX);
				else
					result.add(v+1);
			return new Range(result, MIN, MAX);
		} else if (right == -1) { //decrement
			Set<Integer> result = new HashSet<Integer>();
			Integer values[] = this.values.toArray(new Integer[this.values.size()]);
			for (Integer v : values)
				if (v > MAX) {
					result.add(v);
					result.add(MAX);
				} else if (v <= MIN)
					result.add(MIN);
				else
					result.add(v-1);
			return new Range(result, MIN, MAX);
		} else
			return _plus(abstractMap(right));
	}

	@Override
	public Abstraction _rem(Abstraction right) {
		if (right instanceof Range) {		
			Range op = (Range)right;
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
			return new Range(values, MIN, MAX);
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
	protected Abstraction _rem_reverse(double right) {
		return abstractMap(right)._rem(this);
	}

	@Override
	protected Abstraction _rem_reverse(float right) {
		return abstractMap(right)._rem(this);
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
	public Abstraction _shift_left(Abstraction right) {
		// result is extremely difficult to predict, so this returns TOP
		if (right instanceof Range) {	
			Set<Integer> values = new HashSet<Integer>();
			for (int v = MIN-1; v <= MAX+1; ++v)
				values.add(v);
			return new Range(values, MIN, MAX);	
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
	protected Abstraction _shift_left_reverse(int right) {
		return abstractMap(right)._shift_left(this);
	}

	@Override
	protected Abstraction _shift_left_reverse(long right) {
		return abstractMap(right)._shift_left(this);
	}

	@Override
	public Abstraction _shift_right(Abstraction right) {
		// result is extremely difficult to predict, so this returns TOP
		if (right instanceof Range) {	
			Set<Integer> values = new HashSet<Integer>();
			for (int v = MIN-1; v <= MAX+1; ++v)
				values.add(v);
			return new Range(values, MIN, MAX);	
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
	protected Abstraction _shift_right_reverse(int right) {
		return abstractMap(right)._shift_right(this);
	}

	@Override
	protected Abstraction _shift_right_reverse(long right) {
		return abstractMap(right)._shift_right(this);
	}

	@Override
	public Abstraction _unsigned_shift_right(Abstraction right) {
		// result is extremely difficult to predict, so this returns TOP
		if (right instanceof Range) {	
			Set<Integer> values = new HashSet<Integer>();
			for (int v = MIN-1; v <= MAX+1; ++v)
				values.add(v);
			return new Range(values, MIN, MAX);
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
	protected Abstraction _unsigned_shift_right_reverse(int right) {
		return abstractMap(right)._unsigned_shift_right(this);
	}

	@Override
	protected Abstraction _unsigned_shift_right_reverse(long right) {
		return abstractMap(right)._unsigned_shift_right(this);
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
		if (isComposite()) {
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
	
}
