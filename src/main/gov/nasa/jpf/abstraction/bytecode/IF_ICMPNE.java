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
import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InstructionVisitor;

/**
 * Branch if int comparison succeeds
 * ..., value1, value2 => ...
 */
public class IF_ICMPNE extends gov.nasa.jpf.jvm.bytecode.IF_ICMPNE {

	public IF_ICMPNE(int targetPc) {
		super(targetPc);
	}

	@Override
	public Instruction execute(SystemState ss, KernelState ks, ThreadInfo th) {

		StackFrame sf = th.getTopFrame();

		Abstraction abs_v1 = (Abstraction) sf.getOperandAttr(0);
		Abstraction abs_v2 = (Abstraction) sf.getOperandAttr(1);
		if (abs_v1 == null && abs_v2 == null)
			return super.execute(ss, ks, th);
		else {
			int v1 = th.peek(0);
			int v2 = th.peek(1);

			// abs_v2 != abs_v1
			AbstractBoolean result = Abstraction._ne(v1, abs_v1, v2, abs_v2);
			System.out.printf("IF_ICMPNE> Values: %d (%s), %d (%s)\n", v2,
					abs_v2, v1, abs_v1);

			if (result == AbstractBoolean.TRUE) {
				conditionValue = true;
			} else if (result == AbstractBoolean.FALSE) {
				conditionValue = false;
			} else { // TOP
				ChoiceGenerator<?> cg;
				if (!th.isFirstStepInsn()) { // first time around
					cg = new AbstractChoiceGenerator();
					ss.setNextChoiceGenerator(cg);
					return this;
				} else { // this is what really returns results
					cg = ss.getChoiceGenerator();
					assert (cg instanceof AbstractChoiceGenerator) : "expected AbstractChoiceGenerator, got: "
							+ cg;
					conditionValue = (Integer) cg.getNextChoice() == 0 ? false
							: true;
				}
			}

			th.pop();
			th.pop();

			System.out.println("IF_ICMPNE> Result: " + conditionValue);
			return (conditionValue ? getTarget() : getNext(th));
		}

	}
	
//	@Override
//	public boolean popConditionValue(ThreadInfo ti) {
//		throw new UnsupportedOperationException();
//	}
	
	@Override
	protected Instruction executeBothBranches (SystemState ss, KernelState ks, ThreadInfo ti){
		throw new UnsupportedOperationException();
	}

}
