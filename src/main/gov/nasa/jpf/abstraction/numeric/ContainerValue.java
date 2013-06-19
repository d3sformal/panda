package gov.nasa.jpf.abstraction.numeric;

import gov.nasa.jpf.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ContainerValue extends AbstractValue {
	
	private List<AbstractValue> list;

	public ContainerValue(List<AbstractValue> lst) {
		super(0);
		list = lst;
		// set key
		int key = 0;
		for (int i = 0; i < list.size(); ++i) {
			int cpow = list.get(i).abs.getDomainSize();
			int ckey = list.get(i).getKey();
			key = key * cpow + ckey;
		}

		setKey(key);
	}

	@Override
	public AbstractValue getToken(int idx) {
		int num = getTokensNumber();
		if (idx < 0 || idx >= num)
			throw new RuntimeException("### Error: out of range");
		List<AbstractValue> res = new ArrayList<AbstractValue>();
		for (int i = 0; i < list.size(); ++i)
			if (list.get(i) != null) {
				int cnum = list.get(i).getTokensNumber();
				int cidx = idx % cnum;
				res.add(list.get(i).getToken(cidx));
				idx /= cnum;
			} else
				res.add(null);
		return ((ContainerAbstraction)abs).create(res);
	}

	@Override
	public Set<AbstractValue> getTokens() {
		throw new RuntimeException("get_tokens not implemented");
	}

	@Override
	public int getTokensNumber() {
		int num = 1;
		for (AbstractValue abs : list)
			if (abs != null)
				num *= abs.getTokensNumber();
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
	
	// // // // // // // // // // // // // // // // // // // //
		// numeric operations

		private interface IBinaryOperation {
			AbstractValue execute(AbstractValue left, AbstractValue right);
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
		private static List<Pair<AbstractValue, AbstractValue>> getRelevantAbstractValuePairs(
				ContainerValue op1, ContainerValue op2) {
			List<AbstractValue> lArr = op1.list;
			List<AbstractValue> rArr = op2.list;
			List<Pair<AbstractValue, AbstractValue>> res = new ArrayList<Pair<AbstractValue, AbstractValue>>();

			if (lArr.size() != rArr.size())
				throw new RuntimeException("## Error: wrong container operands");
			// TODO: method must be changed if interactions between different
			// abstractions are meant to be allowed
			for (int i = 0; i < lArr.size(); ++i)
				if (lArr.get(i) == null || rArr.get(i) == null
						|| lArr.get(i).getClass() == rArr.get(i).getClass())
					res.add(new Pair<AbstractValue, AbstractValue>(lArr.get(i), rArr
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
		private AbstractValue binaryOperation(AbstractValue right, IBinaryOperation op) {
			if (right instanceof ContainerValue) {
				ContainerValue right_val = (ContainerValue) right;
				List<AbstractValue> res = new ArrayList<AbstractValue>();

				for (Pair<AbstractValue, AbstractValue> p : getRelevantAbstractValuePairs(
						this, right_val))
					if (p._1 == null || p._2 == null)
						res.add(null);
					else
						res.add(op.execute(p._1, p._2));

				return ((ContainerAbstraction)abs).create(res);
			} else
				throw new RuntimeException("## Error: unknown abstraction");
		}

		@Override
		public AbstractValue _bitwise_and(AbstractValue right) {
			return binaryOperation(right, new IBinaryOperation() {
				@Override
				public AbstractValue execute(AbstractValue left, AbstractValue right) {
					return left._bitwise_and(right);
				}
			});
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
			return binaryOperation(right, new IBinaryOperation() {
				@Override
				public AbstractValue execute(AbstractValue left, AbstractValue right) {
					return left._bitwise_or(right);
				}
			});
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
			return binaryOperation(right, new IBinaryOperation() {
				@Override
				public AbstractValue execute(AbstractValue left, AbstractValue right) {
					return left._bitwise_xor(right);
				}
			});
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
		public AbstractValue _shift_left(AbstractValue right) {
			return binaryOperation(right, new IBinaryOperation() {
				@Override
				public AbstractValue execute(AbstractValue left, AbstractValue right) {
					return left._shift_left(right);
				}
			});
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
		public AbstractValue _shift_right(AbstractValue right) {
			return binaryOperation(right, new IBinaryOperation() {
				@Override
				public AbstractValue execute(AbstractValue left, AbstractValue right) {
					return left._shift_right(right);
				}
			});
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
		public AbstractValue _unsigned_shift_right(AbstractValue right) {
			return binaryOperation(right, new IBinaryOperation() {
				@Override
				public AbstractValue execute(AbstractValue left, AbstractValue right) {
					return left._unsigned_shift_right(right);
				}
			});
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
		public AbstractValue _plus(AbstractValue right) {
			return binaryOperation(right, new IBinaryOperation() {
				@Override
				public AbstractValue execute(AbstractValue left, AbstractValue right) {
					return left._plus(right);
				}
			});
		}

		@Override
		public AbstractValue _plus(int right) {
			return _plus(abs.abstractMap(right));
		}

		@Override
		public AbstractValue _plus(long right) {
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
		public AbstractValue _minus(AbstractValue right) {
			return binaryOperation(right, new IBinaryOperation() {
				@Override
				public AbstractValue execute(AbstractValue left, AbstractValue right) {
					return left._minus(right);
				}
			});
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
		public AbstractValue _minus_reverse(int right) {
			return abs.abstractMap(right)._minus(this);
		}

		@Override
		public AbstractValue _minus_reverse(long right) {
			return abs.abstractMap(right)._minus(this);
		}

		@Override
		public AbstractValue _minus_reverse(float right) {
			return abs.abstractMap(right)._minus(this);
		}

		@Override
		public AbstractValue _minus_reverse(double right) {
			return abs.abstractMap(right)._minus(this);
		}

		@Override
		public AbstractValue _neg() {
			List<AbstractValue> res = new ArrayList<AbstractValue>();
			for (AbstractValue abs : list)
				res.add((abs == null) ? null : abs._neg());
			return ((ContainerAbstraction)abs).create(res);
		}

		@Override
		public AbstractValue _mul(AbstractValue right) {
			return binaryOperation(right, new IBinaryOperation() {
				@Override
				public AbstractValue execute(AbstractValue left, AbstractValue right) {
					return left._mul(right);
				}
			});
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
		public AbstractValue _div(AbstractValue right) {
			return binaryOperation(right, new IBinaryOperation() {
				@Override
				public AbstractValue execute(AbstractValue left, AbstractValue right) {
					return left._div(right);
				}
			});
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
		public AbstractValue _rem(AbstractValue right) {
			return binaryOperation(right, new IBinaryOperation() {
				@Override
				public AbstractValue execute(AbstractValue left, AbstractValue right) {
					return left._rem(right);
				}
			});
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

		// // // // // // // // // // // // // // // // // // // //
		// comparison operations

		private interface IBinaryComparison {
			public AbstractBoolean execute(AbstractValue op1, AbstractValue op2);
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
		private AbstractBoolean binaryComparison(AbstractValue right,
				IBinaryComparison op) {
			if (right instanceof ContainerValue) {
				AbstractBoolean res = AbstractBoolean.FALSE;
				for (Pair<AbstractValue, AbstractValue> p : getRelevantAbstractValuePairs(
						this, (ContainerValue) right))
					if (p._1 != null && p._2 != null)
						res = res.and(op.execute(p._1, p._2));
				return res;
			} else
				throw new RuntimeException("## Error: unknown abstraction");
		}

		@Override
		public AbstractBoolean _lt(AbstractValue right) {
			return binaryComparison(right, new IBinaryComparison() {
				@Override
				public AbstractBoolean execute(AbstractValue op1, AbstractValue op2) {
					return op1._lt(op2);
				}
			});
		}

		@Override
		public AbstractBoolean _lt(int right) {
			return _lt(abs.abstractMap(right));
		}

		@Override
		public AbstractBoolean _le(AbstractValue right) {
			return binaryComparison(right, new IBinaryComparison() {
				@Override
				public AbstractBoolean execute(AbstractValue op1, AbstractValue op2) {
					return op1._le(op2);
				}
			});
		}

		@Override
		public AbstractBoolean _le(int right) {
			return _le(abs.abstractMap(right));
		}

		@Override
		public AbstractBoolean _gt(AbstractValue right) {
			return binaryComparison(right, new IBinaryComparison() {
				@Override
				public AbstractBoolean execute(AbstractValue op1, AbstractValue op2) {
					return op1._gt(op2);
				}
			});
		}

		@Override
		public AbstractBoolean _gt(int right) {
			return _gt(abs.abstractMap(right));
		}

		@Override
		public AbstractBoolean _ge(AbstractValue right) {
			return binaryComparison(right, new IBinaryComparison() {
				@Override
				public AbstractBoolean execute(AbstractValue op1, AbstractValue op2) {
					return op1._ge(op2);
				}
			});
		}

		@Override
		public AbstractBoolean _ge(int right) {
			return _ge(abs.abstractMap(right));
		}

		public AbstractBoolean _eq(AbstractValue right) {
			return binaryComparison(right, new IBinaryComparison() {
				@Override
				public AbstractBoolean execute(AbstractValue op1, AbstractValue op2) {
					return op1._eq(op2);
				}
			});
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
			return SignsAbstraction.getInstance().create(n, z, p);
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
			return SignsAbstraction.getInstance().create(n, z, p);
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
			return SignsAbstraction.getInstance().create(n, z, p);
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
