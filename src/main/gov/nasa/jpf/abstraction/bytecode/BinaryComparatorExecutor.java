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
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.abstraction.impl.NonEmptyAttribute;
import gov.nasa.jpf.abstraction.numeric.SignsAbstraction;
import gov.nasa.jpf.abstraction.numeric.SignsValue;
import gov.nasa.jpf.abstraction.predicate.common.Equals;
import gov.nasa.jpf.abstraction.predicate.common.LessThan;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.choice.IntChoiceFromList;

public abstract class BinaryComparatorExecutor<T> {

	final public Instruction execute(AbstractBinaryOperator<T> cmp, ThreadInfo ti) {
		
		String name = cmp.getClass().getSimpleName();
		
		SystemState ss = ti.getVM().getSystemState();
		StackFrame sf = ti.getModifiableTopFrame();

		Attribute attr1 = getLeftAttribute(sf);
		Attribute attr2 = getRightAttribute(sf);
		
		if (attr1 == null) attr1 = new EmptyAttribute();
		if (attr2 == null) attr2 = new EmptyAttribute();
		
		AbstractValue abs_v1 = attr1.getAbstractValue();
		AbstractValue abs_v2 = attr2.getAbstractValue();
		Expression expr1 = attr1.getExpression();
		Expression expr2 = attr2.getExpression();
		
		Attribute result = null;
		
		if (!ti.isFirstStepInsn()) { // first time around
			if (expr1 != null && expr2 != null && RunDetector.isRunning()) {	
				TruthValue lt = GlobalAbstraction.getInstance().evaluatePredicate(LessThan.create(expr1, expr2));
				TruthValue eq = GlobalAbstraction.getInstance().evaluatePredicate(Equals.create(expr1, expr2));
				TruthValue gt = null;
							
				if (TruthValue.and(lt, eq) == TruthValue.UNDEFINED) {
					gt = TruthValue.UNDEFINED;
				} else if (TruthValue.and(lt, eq) == TruthValue.FALSE) {
					gt = TruthValue.TRUE;
				} else if (lt == TruthValue.TRUE || eq == TruthValue.TRUE) {
					gt = TruthValue.FALSE;
				} else {
					gt = TruthValue.UNKNOWN;
				}
				
				// UNDEFINED MEANS THERE WAS NO ABSTRACTION TO DECIDE THE VALIDITY OF THE PREDICATE
				if (gt != TruthValue.UNDEFINED) {
					result = new NonEmptyAttribute(SignsAbstraction.getInstance().create(lt != TruthValue.FALSE, eq != TruthValue.FALSE, gt != TruthValue.FALSE), null);
	
					System.out.printf("%s> Expressions: %s, %s\n", name, expr1.toString(Notation.DOT_NOTATION), expr2.toString(Notation.DOT_NOTATION));
				}
			}
			
			if (result == null) {		
				T v1 = getLeftOperand(sf);
				T v2 = getRightOperand(sf);
	
				if (abs_v1 == null && abs_v2 == null) {
					return cmp.executeConcrete(ti);
				}
				
				result = cmp.getResult(v1, attr1, v2, attr2);
				
				System.out.printf("%s> Values: %s (%s), %s (%s)\n", name, v2.toString(), abs_v2, v1.toString(), abs_v1);
			}
	
			if (result.getAbstractValue().isComposite()) {
				int size = result.getAbstractValue().getTokensNumber();
				int i = 0;
				int[] choices = new int[size];

				for (AbstractValue choice : result.getAbstractValue().getTokens()) {
					choices[i] = choice.getKey();
					
					++i;
				}

				ChoiceGenerator<?> cg = new IntChoiceFromList("abstractComparisonAll", choices);
				ss.setNextChoiceGenerator(cg);

				return cmp.getSelf();
			}
		} else { // this is what really returns results
			ChoiceGenerator<?> cg = ss.getChoiceGenerator();

			assert (cg instanceof IntChoiceFromList);

			int key = (Integer) cg.getNextChoice();
			
			SignsValue custom = new SignsValue(key);
			
			result = new NonEmptyAttribute(SignsAbstraction.getInstance().create(custom.can_be_NEG(), custom.can_be_ZERO(), custom.can_be_POS()), null);
			
			if (expr1 != null && expr2 != null) {
				Predicate predicate = Equals.create(expr1, expr2);
				
				if (result.getAbstractValue() == SignsAbstraction.NEG) {
					predicate = LessThan.create(expr1, expr2);
				}
				if (result.getAbstractValue() == SignsAbstraction.POS) {
					predicate = LessThan.create(expr2, expr1);
				}
				
				GlobalAbstraction.getInstance().forceValuation(predicate, TruthValue.TRUE);
			}
		}
		
		System.out.printf("%s> Result: %s\n", name, result.getAbstractValue());

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
