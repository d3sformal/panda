package gov.nasa.jpf.abstraction.numeric;

import gov.nasa.jpf.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Container extends Abstraction {

	private List<Abstraction> list = new ArrayList<Abstraction>();

	/**
	 * Gets the list of abstract values, which describe a concrete value with
	 * abstractions specified by configuration. Abstract values are in the same
	 * order as specified. A null value inside the list means that some concrete
	 * value can not be abstracted.
	 * 
	 * @return The list of abstract values.
	 */
	public List<Abstraction> getAbstractionsList() {
		return list;
	}

	@Override
	public Abstraction get_token(int idx) {
		int num = get_num_tokens();
		if (idx < 0 || idx >= num)
			throw new RuntimeException("### Error: out of range");
		List<Abstraction> res = new ArrayList<Abstraction>();
		for (int i = 0; i < list.size(); ++i)
			if (list.get(i) != null) {
				int cnum = list.get(i).get_num_tokens();
				int cidx = idx % cnum;
				res.add(list.get(i).get_token(cidx));
				idx /= cnum;
			} else
				res.add(null);
		return new Container(res);
	}

	@Override
	public Set<Abstraction> get_tokens() {
		throw new RuntimeException("get_tokens not implemented");
	}

	// returns number of tokens in abstract domain
	@Override
	public int get_num_tokens() {
		int num = 1;
		for (Abstraction abs : list)
			if (abs != null)
				num *= abs.get_num_tokens();
		return num;
	}

	@Override
	public boolean isTop() {
		return get_num_tokens() > 1;
	}

	public Container(List<Abstraction> lst) {
		list = lst;
	}

	@Override
	public Abstraction abstract_map(int v) {
		ArrayList<Abstraction> arr = new ArrayList<Abstraction>();
		for (Abstraction abs : list) {
			Abstraction elem = null;
			try {
				elem = abs.abstract_map(v);
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
	public Abstraction abstract_map(float v) {
		ArrayList<Abstraction> arr = new ArrayList<Abstraction>();
		for (Abstraction abs : list) {
			Abstraction elem = null;
			try {
				elem = abs.abstract_map(v);
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
	public Abstraction abstract_map(long v) {
		ArrayList<Abstraction> arr = new ArrayList<Abstraction>();
		for (Abstraction abs : list) {
			Abstraction elem = null;
			try {
				elem = abs.abstract_map(v);
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
	public Abstraction abstract_map(double v) {
		ArrayList<Abstraction> arr = new ArrayList<Abstraction>();
		for (Abstraction abs : list) {
			Abstraction elem = null;
			try {
				elem = abs.abstract_map(v);
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
		return _bitwise_and(abstract_map(right));
	}

	@Override
	public Abstraction _bitwise_and(long right) {
		return _bitwise_and(abstract_map(right));
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
		return _bitwise_or(abstract_map(right));
	}

	@Override
	public Abstraction _bitwise_or(long right) {
		return _bitwise_or(abstract_map(right));
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
		return _bitwise_xor(abstract_map(right));
	}

	@Override
	public Abstraction _bitwise_xor(long right) {
		return _bitwise_xor(abstract_map(right));
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
		return _shift_left(abstract_map(right));
	}

	@Override
	public Abstraction _shift_left(long right) {
		return _shift_left(abstract_map(right));
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
		return _shift_right(abstract_map(right));
	}

	@Override
	public Abstraction _shift_right(long right) {
		return _shift_right(abstract_map(right));
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
		return _unsigned_shift_right(abstract_map(right));
	}

	@Override
	public Abstraction _unsigned_shift_right(long right) {
		return _unsigned_shift_right(abstract_map(right));
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
		return _plus(abstract_map(right));
	}

	@Override
	public Abstraction _plus(long right) {
		return _plus(abstract_map(right));
	}

	@Override
	public Abstraction _plus(float right) {
		return _plus(abstract_map(right));
	}

	@Override
	public Abstraction _plus(double right) {
		return _plus(abstract_map(right));
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
		return _minus(abstract_map(right));
	}

	@Override
	public Abstraction _minus(long right) {
		return _minus(abstract_map(right));
	}

	@Override
	public Abstraction _minus(float right) {
		return _minus(abstract_map(right));
	}

	@Override
	public Abstraction _minus(double right) {
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
	public Abstraction _minus_reverse(float right) {
		return abstract_map(right)._minus(this);
	}

	@Override
	public Abstraction _minus_reverse(double right) {
		return abstract_map(right)._minus(this);
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
		return _mul(abstract_map(right));
	}

	@Override
	public Abstraction _mul(long right) {
		return _mul(abstract_map(right));
	}

	@Override
	public Abstraction _mul(float right) {
		return _mul(abstract_map(right));
	}

	@Override
	public Abstraction _mul(double right) {
		return _mul(abstract_map(right));
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
		return _div(abstract_map(right));
	}

	@Override
	public Abstraction _div(long right) {
		return _div(abstract_map(right));
	}

	@Override
	public Abstraction _div(float right) {
		return _div(abstract_map(right));
	}

	@Override
	public Abstraction _div(double right) {
		return _div(abstract_map(right));
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
		return _rem(abstract_map(right));
	}

	@Override
	public Abstraction _rem(long right) {
		return _rem(abstract_map(right));
	}

	@Override
	public Abstraction _rem(float right) {
		return _rem(abstract_map(right));
	}

	@Override
	public Abstraction _rem(double right) {
		return _rem(abstract_map(right));
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
		return _lt(abstract_map(right));
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
		return _le(abstract_map(right));
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
		return _gt(abstract_map(right));
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
		return _ge(abstract_map(right));
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
		return _eq(abstract_map(right));
	}		
	
	public AbstractBoolean _ne(Abstraction right) {
		return _eq(right).not();
	}

	@Override
	public AbstractBoolean _ne(int right) {
		return _ne(abstract_map(right));
	}

	@Override
	protected Abstraction _div_reverse(int right) {
		return abstract_map(right)._div(this);
	}

	@Override
	protected Abstraction _div_reverse(long right) {
		return abstract_map(right)._div(this);
	}

	@Override
	protected Abstraction _div_reverse(float right) {
		return abstract_map(right)._div(this);
	}

	@Override
	protected Abstraction _div_reverse(double right) {
		return abstract_map(right)._div(this);
	}

	@Override
	protected Abstraction _cmpl_reverse(float right) {
		return abstract_map(right)._cmpl(this);
	}

	@Override
	protected Abstraction _cmpl_reverse(double right) {
		return abstract_map(right)._cmpl(this);
	}

	@Override
	protected Abstraction _cmpg_reverse(float right) {
		return abstract_map(right)._cmpg(this);
	}

	@Override
	protected Abstraction _cmpg_reverse(double right) {
		return abstract_map(right)._cmpg(this);
	}

	@Override
	protected Abstraction _rem_reverse(int right) {
		return abstract_map(right)._rem(this);
	}

	@Override
	protected Abstraction _rem_reverse(long right) {
		return abstract_map(right)._rem(this);
	}

	@Override
	protected Abstraction _rem_reverse(float right) {
		return abstract_map(right)._rem(this);
	}

	@Override
	protected Abstraction _rem_reverse(double right) {
		return abstract_map(right)._rem(this);
	}

	@Override
	protected Abstraction _shift_left_reverse(int right) {
		return abstract_map(right)._shift_left(this);
	}

	@Override
	protected Abstraction _shift_left_reverse(long right) {
		return abstract_map(right)._shift_left(this);
	}

	@Override
	protected Abstraction _shift_right_reverse(int right) {
		return abstract_map(right)._shift_right(this);
	}

	@Override
	protected Abstraction _shift_right_reverse(long right) {
		return abstract_map(right)._shift_right(this);
	}

	@Override
	protected Abstraction _unsigned_shift_right_reverse(int right) {
		return abstract_map(right)._unsigned_shift_right(this);
	}

	@Override
	protected Abstraction _unsigned_shift_right_reverse(long right) {
		return abstract_map(right)._unsigned_shift_right(this);
	}

	@Override
	protected AbstractBoolean _lt_reverse(int right) {
		return abstract_map(right)._lt(this);
	}

	@Override
	protected AbstractBoolean _le_reverse(int right) {
		return abstract_map(right)._le(this);
	}

	@Override
	protected AbstractBoolean _ge_reverse(int right) {
		return abstract_map(right)._ge(this);
	}

	@Override
	protected AbstractBoolean _gt_reverse(int right) {
		return abstract_map(right)._gt(this);
	}
	
	public String toString() {
		if (get_num_tokens() > 1)
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
