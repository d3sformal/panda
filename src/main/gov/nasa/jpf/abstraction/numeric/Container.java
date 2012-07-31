package gov.nasa.jpf.abstraction.numeric;

import gov.nasa.jpf.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Container extends Abstraction {

	List<Abstraction> list = new ArrayList<Abstraction>();

	// returns the abstract token corresponding to the key
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
				System.out.println("### jpf-abstraction: abstract function failure for " 
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
				System.out.println("### jpf-abstraction: abstract function failure for " 
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
				System.out.println("### jpf-abstraction: abstract function failure for " 
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
				System.out.println("### jpf-abstraction: abstract function failure for " 
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

	// may be changed to support interaction between different abstractions
	// (e.g. ZERO + ODD)
	private static List<Pair<Abstraction, Abstraction>> getOperandsCandidates(
			Container op1, Container op2) {
		List<Abstraction> lArr = op1.list;
		List<Abstraction> rArr = op2.list;
		List<Pair<Abstraction, Abstraction>> res = new ArrayList<Pair<Abstraction, Abstraction>>();

		if (lArr.size() != rArr.size())
			throw new RuntimeException("## Error: wrong container operands");

		for (int i = 0; i < lArr.size(); ++i)
			if (lArr.get(i) == null || rArr.get(i) == null
					|| lArr.get(i).getClass() == rArr.get(i).getClass())
				res.add(new Pair<Abstraction, Abstraction>(lArr.get(i), rArr.get(i)));
			else
				throw new RuntimeException("## Error: wrong container operands ('" 
							+ lArr.get(i) + "', '" + rArr.get(i) + "')");

		return res;
	}

	private Abstraction binaryOperation(Abstraction right, IBinaryOperation op) {
		if (right instanceof Container) {
			Container right_val = (Container) right;
			List<Abstraction> res = new ArrayList<Abstraction>();

			for (Pair<Abstraction, Abstraction> p : getOperandsCandidates(this,
					right_val))
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

	private AbstractBoolean binaryComparison(Abstraction right,
			IBinaryComparison op) {
		if (right instanceof Container) {
			AbstractBoolean res = AbstractBoolean.FALSE;
			for (Pair<Abstraction, Abstraction> p : getOperandsCandidates(this,
					(Container) right))
				if (p._1 != null && p._2 != null)
					res = res.or(op.execute(p._1, p._2));
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

	public String toString() {
		if (get_num_tokens() > 1)
			return "TOP";
		else {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (int i = 0; i < list.size(); ++i) {
				sb.append(" " + list.get(i));
				if (i+1 < list.size())
					sb.append(",");
			}
			sb.append(" ]");
			return sb.toString();
		}
	}	
	
}
