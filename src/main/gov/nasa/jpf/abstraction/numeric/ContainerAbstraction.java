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

import gov.nasa.jpf.abstraction.AbstractBoolean;
import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.util.Pair;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * This abstraction is designed to combine other numeric abstractions.
 */
public class ContainerAbstraction extends Abstraction {

	private List<Abstraction> list = new ArrayList<Abstraction>();

	public ContainerAbstraction(List<Abstraction> lst) {
		list = lst;
	}
	
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

	/**
	 * 
	 * @return The number of abstract values in the domain.
	 */
    @Override
	public int getDomainSize() {
		int num = 1;
		for (int i = 0; i < list.size(); ++i)
			if (list.get(i) != null)
				num *= list.get(i).getDomainSize();
/*
			else {
				// get size of domain from global setup
				Abstraction abs = ((Container) AbstractInstructionFactory.abs)
						.getAbstractionsList().get(i);
				key *= abs.getDomainSize();
			}
*/
		return num;
	}
    
    @Override
    public void processPrimitiveStore(Expression from, AccessExpression to) {
    	for (Abstraction abs : list) {
    		abs.processPrimitiveStore(from, to);
    	}
    }
    
    @Override
    public void processObjectStore(Expression from, AccessExpression to) {
    	for (Abstraction abs : list) {
    		abs.processObjectStore(from, to);
    	}
    }
    
    @Override
    public void processMethodCall(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
    	for (Abstraction abs : list) {
    		abs.processMethodCall(threadInfo, before, after);
    	}
	}
	
    @Override
	public void processMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
    	for (Abstraction abs : list) {
    		abs.processMethodReturn(threadInfo, before, after);
    	}
	}
    
    @Override
	public void processVoidMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
    	for (Abstraction abs : list) {
    		abs.processVoidMethodReturn(threadInfo, before, after);
    	}
	}
    
    @Override
    public TruthValue evaluatePredicate(Predicate predicate) {
    	TruthValue ret = TruthValue.UNDEFINED;

    	for (Abstraction abs : list) {
    		TruthValue sub = abs.evaluatePredicate(predicate);
    		
   			ret = TruthValue.or(ret, sub);
    	}
    	
    	return ret;
    }
    
    @Override
    public void forceValuation(Predicate predicate, TruthValue valuation) {
    	for (Abstraction abs : list) {
    		abs.forceValuation(predicate, valuation);
    	}
	}
    
    public ContainerValue create(List<AbstractValue> lst) {
    	ContainerValue res = new ContainerValue(lst);
    	
    	res.abs = this;
    	
    	return res;
    }

	@Override
	public AbstractValue abstractMap(int v) {
		ArrayList<AbstractValue> arr = new ArrayList<AbstractValue>();
		for (Abstraction abs : list) {
			AbstractValue elem = null;
			try {
				elem = abs.abstractMap(v);
			} catch (Exception nie) {
				System.out
						.println("### jpf-abstraction: abstract function failure for "
								+ abs.getClass() + " with " + v);
			}
			arr.add(elem);
		}
		return create(arr);
	}

	@Override
	public AbstractValue abstractMap(float v) {
		ArrayList<AbstractValue> arr = new ArrayList<AbstractValue>();
		for (Abstraction abs : list) {
			AbstractValue elem = null;
			try {
				elem = abs.abstractMap(v);
			} catch (Exception nie) {
				System.out
						.println("### jpf-abstraction: abstract function failure for "
								+ abs.getClass() + " with " + v);
			}
			arr.add(elem);
		}
		return create(arr);
	}

	@Override
	public AbstractValue abstractMap(long v) {
		ArrayList<AbstractValue> arr = new ArrayList<AbstractValue>();
		for (Abstraction abs : list) {
			AbstractValue elem = null;
			try {
				elem = abs.abstractMap(v);
			} catch (Exception nie) {
				System.out
						.println("### jpf-abstraction: abstract function failure for "
								+ abs.getClass() + " with " + v);
			}
			arr.add(elem);
		}
		return create(arr);
	}

	@Override
	public AbstractValue abstractMap(double v) {
		ArrayList<AbstractValue> arr = new ArrayList<AbstractValue>();
		for (Abstraction abs : list) {
			AbstractValue elem = null;
			try {
				elem = abs.abstractMap(v);
			} catch (Exception nie) {
				System.out
						.println("### jpf-abstraction: abstract function failure for "
								+ abs.getClass() + " with " + v);
			}
			arr.add(elem);
		}
		return create(arr);
	}
	
	@Override
	public void start(MethodInfo method) {
		for (Abstraction abs : list) {
			abs.start(method);
		}
	}
	
	@Override
	public void forward(MethodInfo method) {
		for (Abstraction abs : list) {
			abs.forward(method);
		}
	}
	
	@Override
	public void backtrack(MethodInfo method) {
		for (Abstraction abs : list) {
			abs.backtrack(method);
		}
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
		List<AbstractValue> lArr = op1.getAbstractValues();
		List<AbstractValue> rArr = op2.getAbstractValues();
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
	private AbstractValue binaryOperation(AbstractValue left, AbstractValue right, IBinaryOperation op) {
		ContainerValue left_val = (ContainerValue) left;
		ContainerValue right_val = (ContainerValue) right;
		List<AbstractValue> res = new ArrayList<AbstractValue>();
			for (Pair<AbstractValue, AbstractValue> p : getRelevantAbstractValuePairs(
				left_val, right_val))
			if (p._1 == null || p._2 == null)
				res.add(null);
			else
				res.add(op.execute(p._1, p._2));
			return ((ContainerAbstraction)left.abs).create(res);
	}
	
	@Override
	public AbstractValue _bitwise_and(AbstractValue left, AbstractValue right) {
		return binaryOperation(left, right, new IBinaryOperation() {
			@Override
			public AbstractValue execute(AbstractValue left, AbstractValue right) {
				return left.abs._bitwise_and(left, right);
			}
		});
	}

	@Override
	public AbstractValue _bitwise_or(AbstractValue left, AbstractValue right) {
		return binaryOperation(left, right, new IBinaryOperation() {
			@Override
			public AbstractValue execute(AbstractValue left, AbstractValue right) {
				return left.abs._bitwise_or(left, right);
			}
		});
	}

	@Override
	public AbstractValue _bitwise_xor(AbstractValue left, AbstractValue right) {
		return binaryOperation(left, right, new IBinaryOperation() {
			@Override
			public AbstractValue execute(AbstractValue left, AbstractValue right) {
				return left.abs._bitwise_xor(left, right);
			}
		});
	}

	@Override
	public AbstractValue _shift_left(AbstractValue left, AbstractValue right) {
		return binaryOperation(left, right, new IBinaryOperation() {
			@Override
			public AbstractValue execute(AbstractValue left, AbstractValue right) {
				return left.abs._shift_left(left, right);
			}
		});
	}

	@Override
	public AbstractValue _shift_right(AbstractValue left, AbstractValue right) {
		return binaryOperation(left, right, new IBinaryOperation() {
			@Override
			public AbstractValue execute(AbstractValue left, AbstractValue right) {
				return left.abs._shift_right(left, right);
			}
		});
	}

	@Override
	public AbstractValue _unsigned_shift_right(AbstractValue left, AbstractValue right) {
		return binaryOperation(left, right, new IBinaryOperation() {
			@Override
			public AbstractValue execute(AbstractValue left, AbstractValue right) {
				return left.abs._unsigned_shift_right(left, right);
			}
		});
	}

	@Override
	public AbstractValue _plus(AbstractValue left, AbstractValue right) {
		return binaryOperation(left, right, new IBinaryOperation() {
			@Override
			public AbstractValue execute(AbstractValue left, AbstractValue right) {
				return left.abs._plus(left, right);
			}
		});
	}

	@Override
	public AbstractValue _minus(AbstractValue left, AbstractValue right) {
		return binaryOperation(left, right, new IBinaryOperation() {
			@Override
			public AbstractValue execute(AbstractValue left, AbstractValue right) {
				return left.abs._minus(left, right);
			}
		});
	}

	@Override
	public AbstractValue _neg_impl(AbstractValue left) {
		List<AbstractValue> res = new ArrayList<AbstractValue>();
		ContainerValue left_val = (ContainerValue) left;
		for (AbstractValue abs : left_val.getAbstractValues())
			res.add((abs == null) ? null : abs.abs._neg_impl(left));
		return ((ContainerAbstraction)left.abs).create(res);
	}

	@Override
	public AbstractValue _mul(AbstractValue left, AbstractValue right) {
		return binaryOperation(left, right, new IBinaryOperation() {
			@Override
			public AbstractValue execute(AbstractValue left, AbstractValue right) {
				return left.abs._mul(left, right);
			}
		});
	}

	@Override
	public AbstractValue _div(AbstractValue left, AbstractValue right) {
		return binaryOperation(left, right, new IBinaryOperation() {
			@Override
			public AbstractValue execute(AbstractValue left, AbstractValue right) {
				return left.abs._div(left, right);
			}
		});
	}

	@Override
	public AbstractValue _rem(AbstractValue left, AbstractValue right) {
		return binaryOperation(left, right, new IBinaryOperation() {
			@Override
			public AbstractValue execute(AbstractValue left, AbstractValue right) {
				return left.abs._rem(left, right);
			}
		});
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
	private AbstractBoolean binaryComparison(AbstractValue left, AbstractValue right,
			IBinaryComparison op) {
		AbstractBoolean res = AbstractBoolean.FALSE;
		for (Pair<AbstractValue, AbstractValue> p : getRelevantAbstractValuePairs(
				(ContainerValue) left, (ContainerValue) right))
			if (p._1 != null && p._2 != null)
				res = res.and(op.execute(p._1, p._2));
		return res;
	}

	@Override
	public AbstractBoolean _lt(AbstractValue left, AbstractValue right) {
		return binaryComparison(left, right, new IBinaryComparison() {
			@Override
			public AbstractBoolean execute(AbstractValue left, AbstractValue right) {
				return left.abs._lt(left, right);
			}
		});
	}

	@Override
	public AbstractBoolean _le(AbstractValue left, AbstractValue right) {
		return binaryComparison(left, right, new IBinaryComparison() {
			@Override
			public AbstractBoolean execute(AbstractValue left, AbstractValue right) {
				return left.abs._le(left, right);
			}
		});
	}

	@Override
	public AbstractBoolean _gt(AbstractValue left, AbstractValue right) {
		return binaryComparison(left, right, new IBinaryComparison() {
			@Override
			public AbstractBoolean execute(AbstractValue left, AbstractValue right) {
				return left.abs._gt(left, right);
			}
		});
	}

	@Override
	public AbstractBoolean _ge(AbstractValue left, AbstractValue right) {
		return binaryComparison(left, right, new IBinaryComparison() {
			@Override
			public AbstractBoolean execute(AbstractValue left, AbstractValue right) {
				return left.abs._ge(left, right);
			}
		});
	}

	@Override
	public AbstractBoolean _eq(AbstractValue left, AbstractValue right) {
		return binaryComparison(left, right, new IBinaryComparison() {
			@Override
			public AbstractBoolean execute(AbstractValue left, AbstractValue right) {
				return left.abs._eq(left, right);
			}
		});
	}

	@Override
	public AbstractBoolean _ne(AbstractValue left, AbstractValue right) {
		return _eq(left, right).not();
	}

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         AbstractValue; Signs.NEG if this AbstractValue is numerically less
	 *         than the operand; and Signs.POS if this AbstractValue is
	 *         numerically greater than the operand.
	 */
	@Override
	public SignsValue _cmp(AbstractValue left, AbstractValue right) {
		boolean n = false, z = false, p = false;
		if (_gt(left, right) != AbstractBoolean.FALSE)
			p = true;
		if (_lt(left, right) != AbstractBoolean.FALSE)
			n = true;
		if (_gt(left, right) != AbstractBoolean.TRUE
				&& _lt(left, right) != AbstractBoolean.TRUE)
			z = true;
		return SignsAbstraction.getInstance().create(n, z, p);
	}

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         AbstractValue; Signs.NEG if this AbstractValue is numerically less
	 *         than the operand; and Signs.POS if this AbstractValue is
	 *         numerically greater than the operand.
	 */
	@Override
	public SignsValue _cmpg(AbstractValue left, AbstractValue right) {
		boolean n = false, z = false, p = false;
		if (_gt(left, right) != AbstractBoolean.FALSE)
			p = true;
		if (_lt(left, right) != AbstractBoolean.FALSE)
			n = true;
		if (_gt(left, right) != AbstractBoolean.TRUE
				&& _lt(left, right) != AbstractBoolean.TRUE)
			z = true;
		return SignsAbstraction.getInstance().create(n, z, p);
	}

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         AbstractValue; Signs.NEG if this AbstractValue is numerically less
	 *         than the operand; and Signs.POS if this AbstractValue is
	 *         numerically greater than the operand.
	 */
	@Override
	public SignsValue _cmpl(AbstractValue left, AbstractValue right) {
		boolean n = false, z = false, p = false;
		if (_gt(left, right) != AbstractBoolean.FALSE)
			p = true;
		if (_lt(left, right) != AbstractBoolean.FALSE)
			n = true;
		if (_gt(left, right) != AbstractBoolean.TRUE
				&& _lt(left, right) != AbstractBoolean.TRUE)
			z = true;
		return SignsAbstraction.getInstance().create(n, z, p);
	}

}