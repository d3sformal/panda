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
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.KernelState;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;

/**
 * Branch if int comparison succeeds
 * ..., value1, value2 => ...
 */
public class IF_ICMPEQ extends gov.nasa.jpf.jvm.bytecode.IF_ICMPEQ {

	public IF_ICMPEQ(int targetPc) {
		super(targetPc);
	}

	@Override
	public Instruction execute(ThreadInfo ti) {

		SystemState ss = ti.getVM().getSystemState();
		StackFrame sf = ti.getModifiableTopFrame();

		AbstractValue abs_v1 = (AbstractValue) sf.getOperandAttr(0);
		AbstractValue abs_v2 = (AbstractValue) sf.getOperandAttr(1);

		if (abs_v1 == null && abs_v2 == null) {
			return super.execute(ti);
		}

		int v1 = sf.peek(0);
		int v2 = sf.peek(1);

		// abs_v2 == abs_v1
		AbstractBoolean result = AbstractValue._eq(v1, abs_v1, v2, abs_v2);

		System.out.printf("IF_ICMPEQ> Values: %d (%s), %d (%s)\n", v2, abs_v2, v1, abs_v1);

		if (result == AbstractBoolean.TRUE) {
			conditionValue = true;
		} else if (result == AbstractBoolean.FALSE) {
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

		sf.pop();
		sf.pop();

		System.out.println("IF_ICMPEQ> Result: " + conditionValue);

		return (conditionValue ? getTarget() : getNext(ti));
	}
	
	@Override
	protected Instruction executeBothBranches (SystemState ss, KernelState ks, ThreadInfo ti){
		throw new UnsupportedOperationException();
	}

}
