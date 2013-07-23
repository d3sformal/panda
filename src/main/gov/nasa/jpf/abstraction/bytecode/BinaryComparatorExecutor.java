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
//
package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.FocusAbstractChoiceGenerator;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.abstraction.impl.NonEmptyAttribute;
import gov.nasa.jpf.abstraction.numeric.SignsAbstraction;
import gov.nasa.jpf.abstraction.numeric.SignsValue;
import gov.nasa.jpf.abstraction.predicate.common.Equals;
import gov.nasa.jpf.abstraction.predicate.common.LessThan;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

public abstract class BinaryComparatorExecutor<T> {

	final public Instruction execute(AbstractBinaryOperator<T> cmp, ThreadInfo ti) {
		
		String name = cmp.getClass().getName();
		
		SystemState ss = ti.getVM().getSystemState();
		StackFrame sf = ti.getModifiableTopFrame();

		Attribute attr1 = getLeftAttribute(sf);
		Attribute attr2 = getRightAttribute(sf);
		
		AbstractValue abs_v1 = null;
		AbstractValue abs_v2 = null;
		
		if (attr1 == null) attr1 = new EmptyAttribute();
		if (attr2 == null) attr2 = new EmptyAttribute();
		
		Attribute result;
		
		if (attr1.getExpression() != null && attr2.getExpression() != null) {
			if (ti.isFirstStepInsn()) {
				ChoiceGenerator<?> cg = ss.getChoiceGenerator();
				
				Integer comparison = (Integer) cg.getNextChoice();
				
				result = new NonEmptyAttribute(new SignsValue(comparison), Constant.create(comparison));
				
				storeResult(result, sf);
				
				return cmp.getSelf();
			}
			
			TruthValue lt = GlobalAbstraction.getInstance().processBranching(LessThan.create(attr1.getExpression(), attr2.getExpression()));
			TruthValue eq = GlobalAbstraction.getInstance().processBranching(Equals.create(attr1.getExpression(), attr2.getExpression()));
			TruthValue gt = TruthValue.or(lt, eq) == TruthValue.UNKNOWN ? TruthValue.UNKNOWN : TruthValue.TRUE; 
			
			if (lt == TruthValue.TRUE) {
				result = new NonEmptyAttribute(Abstraction._cmpg(0, abs_v1, 1, abs_v2), Constant.create(1));
				
				storeResult(result, sf);

				return cmp.getNext(ti);
			}
			
			if (eq == TruthValue.TRUE) {
				result = new NonEmptyAttribute(Abstraction._cmpg(0, abs_v1, 0, abs_v2), Constant.create(0));
				
				storeResult(result, sf);

				return cmp.getNext(ti);
			}
			
			if (gt == TruthValue.TRUE) {
				result = new NonEmptyAttribute(Abstraction._cmpg(1, abs_v1, 0, abs_v2), Constant.create(-1));
				
				storeResult(result, sf);

				return cmp.getNext(ti);
			}
			
			ChoiceGenerator<?> cg = new FocusAbstractChoiceGenerator(SignsAbstraction.getInstance().getDomainSize());
			ss.setNextChoiceGenerator(cg);
			
			return cmp.getSelf();
		}

		abs_v1 = attr1.getAbstractValue();
		abs_v2 = attr2.getAbstractValue();
		
		T v1 = getLeftOperand(sf);
		T v2 = getRightOperand(sf);

		result = cmp.getResult(v1, attr1, v2, attr2);

		if (abs_v1 == null && abs_v2 == null) {
			Instruction ret = cmp.executeConcrete(ti);
			
			storeAttribute(result, sf);
			
			return ret;
		}

		System.out.printf("%s> Values: %s (%s), %s (%s)\n", name, v2.toString(), abs_v2, v1.toString(), abs_v1);

		if (result.getAbstractValue().isComposite()) {
			if (!ti.isFirstStepInsn()) { // first time around
				int size = result.getAbstractValue().getTokensNumber();
				ChoiceGenerator<?> cg = new FocusAbstractChoiceGenerator(size);
				ss.setNextChoiceGenerator(cg);

				return cmp.getSelf();
			} else { // this is what really returns results
				ChoiceGenerator<?> cg = ss.getChoiceGenerator();

				assert (cg instanceof FocusAbstractChoiceGenerator);

				int key = (Integer) cg.getNextChoice();
				result.setAbstractValue(result.getAbstractValue().getToken(key));
			}
		}
		
		System.out.printf("%s> Result: %s\n", name, result);

		storeResult(result, sf);

		return cmp.getNext(ti);
	}
	
	protected Attribute getAttribute(StackFrame sf, int index) {
		return (Attribute)sf.getOperandAttr(index);
	}

	abstract protected Attribute getLeftAttribute(StackFrame sf);
	abstract protected Attribute getRightAttribute(StackFrame sf);
	abstract protected T getLeftOperand(StackFrame sf);
	abstract protected T getRightOperand(StackFrame sf);
	abstract protected void storeAttribute(Attribute result, StackFrame sf);
	abstract protected void storeResult(Attribute result, StackFrame sf);
}
