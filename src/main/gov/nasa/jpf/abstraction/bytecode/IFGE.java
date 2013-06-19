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

import gov.nasa.jpf.abstraction.numeric.AbstractBoolean;
import gov.nasa.jpf.abstraction.numeric.AbstractChoiceGenerator;
import gov.nasa.jpf.abstraction.numeric.AbstractValue;
import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;

/**
 * Branch if int comparison with zero succeeds 
 * ..., value => ...
 */
public class IFGE extends gov.nasa.jpf.jvm.bytecode.IFGE {

	public IFGE(int targetPc) {
		super(targetPc);
	}

	@Override
	public Instruction execute(ThreadInfo ti) {

		SystemState ss = ti.getVM().getSystemState();
		StackFrame sf = ti.getModifiableTopFrame();
		AbstractValue abs_v = (AbstractValue) sf.getOperandAttr();

		if (abs_v == null) { // the condition is concrete
			return super.execute(ti);
		}
		
		// the condition is abstract

		System.out.printf("IFGE> Values: %d (%s)\n", sf.peek(0), abs_v);
		
		AbstractBoolean abs_condition = Abstraction._ge(0, abs_v, 0, null);

		if (abs_condition == AbstractBoolean.TRUE) {
			conditionValue = true;
		} else if (abs_condition == AbstractBoolean.FALSE) {
			conditionValue = false;
		} else { // TOP
			if (!ti.isFirstStepInsn()) { // first time around
				ChoiceGenerator<?> cg = new AbstractChoiceGenerator();
				ss.setNextChoiceGenerator(cg);

				return this;
			} else { // this is what really returns results
				ChoiceGenerator<?> cg = ss.getChoiceGenerator();
					
				assert (cg instanceof AbstractChoiceGenerator) : "expected AbstractChoiceGenerator, got: " + cg;
				
				conditionValue = (Integer) cg.getNextChoice() == 0 ? false : true;
			}
		}

		System.out.println("IFGE> Result: " + conditionValue);

		sf.pop();
			
		return (conditionValue ? getTarget() : getNext(ti));
	}
}
