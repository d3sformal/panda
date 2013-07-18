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
import gov.nasa.jpf.abstraction.FocusAbstractChoiceGenerator;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

public abstract class UnaryOperatorExecutor<T> {

	final public Instruction execute(AbstractUnaryOperator<T> op, ThreadInfo ti) {

		String name = op.getClass().getName();
		
		SystemState ss = ti.getVM().getSystemState();
		StackFrame sf = ti.getModifiableTopFrame();

		Attribute attr = getAttribute(sf);

		AbstractValue abs_v = null;
		
		if (attr == null) attr = new EmptyAttribute();
		
		abs_v = attr.getAbstractValue();
		
		T v = getOperand(sf);

		Attribute result = op.getResult(v, attr);

		if (abs_v == null) {
			Instruction ret = op.executeConcrete(ti);
			
			storeAttribute(result, sf);
			
			return ret;
		}

		System.out.printf("%s> Values: %s (%s)\n", name, v.toString(), abs_v);

		if (result.getAbstractValue().isComposite()) {
			if (!ti.isFirstStepInsn()) { // first time around
				int size = result.getAbstractValue().getTokensNumber();
				ChoiceGenerator<?> cg = new FocusAbstractChoiceGenerator(size);
				ss.setNextChoiceGenerator(cg);

				return op.getSelf();
			} else { // this is what really returns results
				ChoiceGenerator<?> cg = ss.getChoiceGenerator();

				assert (cg instanceof FocusAbstractChoiceGenerator);

				int key = (Integer) cg.getNextChoice();
				result.setAbstractValue(result.getAbstractValue().getToken(key));
			}
		}
		
		System.out.printf("%s> Result: %s\n", name, result);

		storeResult(result, sf);

		return op.getNext(ti);
	}
	
	protected Attribute getAttribute(StackFrame sf, int index) {
		return (Attribute)sf.getOperandAttr(index);
	}
	
	abstract protected Attribute getAttribute(StackFrame sf);
	abstract protected T getOperand(StackFrame sf);
	abstract protected void storeAttribute(Attribute result, StackFrame sf);
	abstract protected void storeResult(Attribute result, StackFrame sf);
}
