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

import gov.nasa.jpf.abstraction.AbstractInstructionFactory;
import gov.nasa.jpf.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This abstraction is designed to combine other numeric abstractions.
 */
public class Container extends Abstraction {

	private List<Abstraction> list = new ArrayList<Abstraction>();

	/**
	 * Gets the list of abstract values, which describe a concrete value with
	 * abstractions specified by configuration. Abstract values are in the same
	 * order as specified. A null value inside the list means that some concrete
	 * value could not have been abstracted.
	 * 
	 * @return The list of abstract values.
	 */
	public List<Abstraction> getAbstractionsList() {
		return list;
	}

	@Override
	public Abstraction getToken(int idx) {
		int num = getTokensNumber();
		if (idx < 0 || idx >= num)
			throw new RuntimeException("### Error: out of range");
		List<Abstraction> res = new ArrayList<Abstraction>();
		for (int i = 0; i < list.size(); ++i)
			if (list.get(i) != null) {
				int cnum = list.get(i).getTokensNumber();
				int cidx = idx % cnum;
				res.add(list.get(i).getToken(cidx));
				idx /= cnum;
			} else
				res.add(null);
		return new Container(res);
	}

	@Override
	public Set<Abstraction> getTokens() {
		throw new RuntimeException("get_tokens not implemented");
	}

	@Override
	public int getTokensNumber() {
		int num = 1;
		for (Abstraction abs : list)
			if (abs != null)
				num *= abs.getTokensNumber();
		return num;
	}

	/**
	 * 
	 * @return The number of abstract values in the domain.
	 */
	public int getDomainSize() {
		int num = 1;
		for (int i = 0; i < list.size(); ++i)
			if (list.get(i) != null)
				num *= list.get(i).getDomainSize();
			else {
				// get size of domain from global setup
				Abstraction abs = ((Container) AbstractInstructionFactory.abs)
						.getAbstractionsList().get(i);
				key *= abs.getDomainSize();
			}
		return num;
	}

	/**
	 * @return true, if this abstraction is a single value from the domain;
	 *         false, if this abstraction represents a set of values from the
	 *         domain.
	 */
	@Override
	public boolean isComposite() {
		return getTokensNumber() > 1;
	}

	public Container(List<Abstraction> lst) {
		super(0);
		list = lst;
		// set key
		int key = 0;
		for (int i = 0; i < list.size(); ++i)
			if (list.get(i) != null) {
				int cpow = list.get(i).getDomainSize();
				int ckey = list.get(i).getKey();
				key = key * cpow + ckey;
			} else {
				// get size of domain from global setup
				Abstraction abs = ((Container) AbstractInstructionFactory.abs)
						.getAbstractionsList().get(i);
				key *= abs.getDomainSize();
			}
		setKey(key);
	}

	@Override
	public Abstraction abstractMap(int v) {
		ArrayList<Abstraction> arr = new ArrayList<Abstraction>();
		for (Abstraction abs : list) {
			Abstraction elem = null;
			try {
				elem = abs.abstractMap(v);
			} catch (Exception nie) {
				System.out
						.println("### jpf-abstraction: abstract function failure for "
								+ abs.getClass() + " with " + v);
			}
			arr.add(elem);
		}
		return new Container(arr);
	}

	@Override
	public Abstraction abstractMap(float v) {
		ArrayList<Abstraction> arr = new ArrayList<Abstraction>();
		for (Abstraction abs : list) {
			Abstraction elem = null;
			try {
				elem = abs.abstractMap(v);
			} catch (Exception nie) {
				System.out
						.println("### jpf-abstraction: abstract function failure for "
								+ abs.getClass() + " with " + v);
			}
			arr.add(elem);
		}
		return new Container(arr);
	}

	@Override
	public Abstraction abstractMap(long v) {
		ArrayList<Abstraction> arr = new ArrayList<Abstraction>();
		for (Abstraction abs : list) {
			Abstraction elem = null;
			try {
				elem = abs.abstractMap(v);
			} catch (Exception nie) {
				System.out
						.println("### jpf-abstraction: abstract function failure for "
								+ abs.getClass() + " with " + v);
			}
			arr.add(elem);
		}
		return new Container(arr);
	}

	@Override
	public Abstraction abstractMap(double v) {
		ArrayList<Abstraction> arr = new ArrayList<Abstraction>();
		for (Abstraction abs : list) {
			Abstraction elem = null;
			try {
				elem = abs.abstractMap(v);
			} catch (Exception nie) {
				System.out
						.println("### jpf-abstraction: abstract function failure for "
								+ abs.getClass() + " with " + v);
			}
			arr.add(elem);
		}
		return new Container(arr);
	}

	// // // // // // // // // // // // // // // // // // // //
	// numeric operations

	private interface IBinaryOperation {
		Abstraction execute(Abstraction left, Abstraction right);
	}

	/**
	 * Gets all pairs of abstractions from two containers respectfully, which
	 * can interact which each other. Mainly used for performing a binary
	 * operation on them and construction of a new container abstraction as a
	 * result.
	 * 
	 * @param op1
	 *            The first container.
	 * @param op2
	 *            The second container.
	 * @return A list of pairs of abstractions from op1 and op2 containers
	 *         respectively, which can interact with each other.
	 */
	private static List<Pair<Abstraction, Abstraction>> getRelevantAbstractionPairs(
			Container op1, Container op2) {
		List<Abstraction> lArr = op1.list;
		List<Abstraction> rArr = op2.list;
		List<Pair<Abstraction, Abstraction>> res = new ArrayList<Pair<Abstraction, Abstraction>>();

		if (lArr.size() != rArr.size())
			throw new RuntimeException("## Error: wrong container operands");
		// TODO: method must be changed if interactions between different
		// abstractions are meant to be allowed
		for (int i = 0; i < lArr.size(); ++i)
			if (lArr.get(i) == null || rArr.get(i) == null
					|| lArr.get(i).getClass() == rArr.get(i).getClass())
				res.add(new Pair<Abstraction, Abstraction>(lArr.get(i), rArr
						.get(i)));
			else
				throw new RuntimeException(
						"## Error: wrong container operands ('" + lArr.get(i)
								+ "', '" + rArr.get(i) + "')");
		return res;
	}

	/**
	 * Performs a binary operation on two operands (this and right) specified by
	 * IBinaryOperation.execute method.
	 * 
	 * @param right
	 *            The second container.
	 * @param op
	 *            Implementation of IBinaryOperation interface with a binary
	 *            operation on abstractions.
	 * @return The result of an operation.
	 */
	private Abstraction binaryOperation(Abstraction right, IBinaryOperation op) {
		if (right instanceof Container) {
			Container right_val = (Container) right;
			List<Abstraction> res = new ArrayList<Abstraction>();

			for (Pair<Abstraction, Abstraction> p : getRelevantAbstractionPairs(
					this, right_val))
				if (p._1 == null || p._2 == null)
					res.add(null);
				else
					res.add(op.execute(p._1, p._2));

			return new Container(res);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _bitwise_and(Abstraction right) {
		return binaryOperation(right, new IBinaryOperation() {
			@Override
			public Abstraction execute(Abstraction left, Abstraction right) {
				return left._bitwise_and(right);
			}
		});
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
		return binaryOperation(right, new IBinaryOperation() {
			@Override
			public Abstraction execute(Abstraction left, Abstraction right) {
				return left._bitwise_or(right);
			}
		});
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
		return binaryOperation(right, new IBinaryOperation() {
			@Override
			public Abstraction execute(Abstraction left, Abstraction right) {
				return left._bitwise_xor(right);
			}
		});
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
	public Abstraction _shift_left(Abstraction right) {
		return binaryOperation(right, new IBinaryOperation() {
			@Override
			public Abstraction execute(Abstraction left, Abstraction right) {
				return left._shift_left(right);
			}
		});
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
	public Abstraction _shift_right(Abstraction right) {
		return binaryOperation(right, new IBinaryOperation() {
			@Override
			public Abstraction execute(Abstraction left, Abstraction right) {
				return left._shift_right(right);
			}
		});
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
	public Abstraction _unsigned_shift_right(Abstraction right) {
		return binaryOperation(right, new IBinaryOperation() {
			@Override
			public Abstraction execute(Abstraction left, Abstraction right) {
				return left._unsigned_shift_right(right);
			}
		});
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
	public Abstraction _plus(Abstraction right) {
		return binaryOperation(right, new IBinaryOperation() {
			@Override
			public Abstraction execute(Abstraction left, Abstraction right) {
				return left._plus(right);
			}
		});
	}

	@Override
	public Abstraction _plus(int right) {
		return _plus(abstractMap(right));
	}

	@Override
	public Abstraction _plus(long right) {
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
	public Abstraction _minus(Abstraction right) {
		return binaryOperation(right, new IBinaryOperation() {
			@Override
			public Abstraction execute(Abstraction left, Abstraction right) {
				return left._minus(right);
			}
		});
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
	public Abstraction _minus_reverse(int right) {
		return abstractMap(right)._minus(this);
	}

	@Override
	public Abstraction _minus_reverse(long right) {
		return abstractMap(right)._minus(this);
	}

	@Override
	public Abstraction _minus_reverse(float right) {
		return abstractMap(right)._minus(this);
	}

	@Override
	public Abstraction _minus_reverse(double right) {
		return abstractMap(right)._minus(this);
	}

	@Override
	public Abstraction _neg() {
		List<Abstraction> res = new ArrayList<Abstraction>();
		for (Abstraction abs : list)
			res.add((abs == null) ? null : abs._neg());
		return new Container(res);
	}

	@Override
	public Abstraction _mul(Abstraction right) {
		return binaryOperation(right, new IBinaryOperation() {
			@Override
			public Abstraction execute(Abstraction left, Abstraction right) {
				return left._mul(right);
			}
		});
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
	public Abstraction _div(Abstraction right) {
		return binaryOperation(right, new IBinaryOperation() {
			@Override
			public Abstraction execute(Abstraction left, Abstraction right) {
				return left._div(right);
			}
		});
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
	public Abstraction _rem(Abstraction right) {
		return binaryOperation(right, new IBinaryOperation() {
			@Override
			public Abstraction execute(Abstraction left, Abstraction right) {
				return left._rem(right);
			}
		});
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

	// // // // // // // // // // // // // // // // // // // //
	// comparison operations

	private interface IBinaryComparison {
		public AbstractBoolean execute(Abstraction op1, Abstraction op2);
	}

	/**
	 * Performs a comparison of two operands (this and right) specified by
	 * IBinaryComparison.execute method.
	 * 
	 * @param right
	 *            The second container.
	 * @param op
	 *            Implementation of IBinaryComparison interface which compares
	 *            two abstract values.
	 * @return The result of comparison.
	 */
	private AbstractBoolean binaryComparison(Abstraction right,
			IBinaryComparison op) {
		if (right instanceof Container) {
			AbstractBoolean res = AbstractBoolean.FALSE;
			for (Pair<Abstraction, Abstraction> p : getRelevantAbstractionPairs(
					this, (Container) right))
				if (p._1 != null && p._2 != null)
					res = res.and(op.execute(p._1, p._2));
			return res;
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _lt(Abstraction right) {
		return binaryComparison(right, new IBinaryComparison() {
			@Override
			public AbstractBoolean execute(Abstraction op1, Abstraction op2) {
				return op1._lt(op2);
			}
		});
	}

	@Override
	public AbstractBoolean _lt(int right) {
		return _lt(abstractMap(right));
	}

	@Override
	public AbstractBoolean _le(Abstraction right) {
		return binaryComparison(right, new IBinaryComparison() {
			@Override
			public AbstractBoolean execute(Abstraction op1, Abstraction op2) {
				return op1._le(op2);
			}
		});
	}

	@Override
	public AbstractBoolean _le(int right) {
		return _le(abstractMap(right));
	}

	@Override
	public AbstractBoolean _gt(Abstraction right) {
		return binaryComparison(right, new IBinaryComparison() {
			@Override
			public AbstractBoolean execute(Abstraction op1, Abstraction op2) {
				return op1._gt(op2);
			}
		});
	}

	@Override
	public AbstractBoolean _gt(int right) {
		return _gt(abstractMap(right));
	}

	@Override
	public AbstractBoolean _ge(Abstraction right) {
		return binaryComparison(right, new IBinaryComparison() {
			@Override
			public AbstractBoolean execute(Abstraction op1, Abstraction op2) {
				return op1._ge(op2);
			}
		});
	}

	@Override
	public AbstractBoolean _ge(int right) {
		return _ge(abstractMap(right));
	}

	public AbstractBoolean _eq(Abstraction right) {
		return binaryComparison(right, new IBinaryComparison() {
			@Override
			public AbstractBoolean execute(Abstraction op1, Abstraction op2) {
				return op1._eq(op2);
			}
		});
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
		if (getTokensNumber() > 1)
			return "TOP";
		else {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (int i = 0; i < list.size(); ++i) {
				sb.append(" " + list.get(i));
				if (i + 1 < list.size())
					sb.append(",");
			}
			sb.append(" ]");
			return sb.toString();
		}
	}

}
