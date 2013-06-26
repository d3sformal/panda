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
import gov.nasa.jpf.abstraction.FocusAbstractChoiceGenerator;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

public abstract class BinaryOperatorExecutor<T> {

	final public Instruction execute(AbstractBinaryOperator<T> op, ThreadInfo ti) {
		
		String name = op.getClass().getName();
		
		SystemState ss = ti.getVM().getSystemState();
		StackFrame sf = ti.getModifiableTopFrame();

		AbstractValue abs_v1 = getLeftAbstractValue(sf);
		AbstractValue abs_v2 = getRightAbstractValue(sf);

		if (abs_v1 == null && abs_v2 == null) {
			return op.executeConcrete(ti);
		}

		T v1 = getLeftOperand(sf);
		T v2 = getRightOperand(sf);

		AbstractValue result = op.getResult(v1, abs_v1, v2, abs_v2);

		System.out.printf("%s> Values: %s (%s), %s (%s)\n", name, v2.toString(), abs_v2, v1.toString(), abs_v1);

		if (result.isComposite()) {
			if (!ti.isFirstStepInsn()) { // first time around
				int size = result.getTokensNumber();
				ChoiceGenerator<?> cg = new FocusAbstractChoiceGenerator(size);
				ss.setNextChoiceGenerator(cg);

				return op.getSelf();
			} else { // this is what really returns results
				ChoiceGenerator<?> cg = ss.getChoiceGenerator();

				assert (cg instanceof FocusAbstractChoiceGenerator);

				int key = (Integer) cg.getNextChoice();
				result = result.getToken(key);
			}
		}
		
		System.out.printf("%s> Result: %s\n", name, result);

		storeResult(result, sf);

		return op.getNext(ti);
	}
	
	abstract protected AbstractValue getLeftAbstractValue(StackFrame sf);
	abstract protected AbstractValue getRightAbstractValue(StackFrame sf);
	abstract protected T getLeftOperand(StackFrame sf);
	abstract protected T getRightOperand(StackFrame sf);
	abstract protected void storeResult(AbstractValue result, StackFrame sf);
}
