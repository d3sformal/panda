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

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.abstraction.numeric.FocusAbstractChoiceGenerator;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.KernelState;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;

/**
 * Divide long
 * ..., value1, value2 => ..., result
 */
public class LDIV extends gov.nasa.jpf.jvm.bytecode.LDIV {

	@Override
	public Instruction execute(ThreadInfo ti) {
		
		SystemState ss = ti.getVM().getSystemState();
		StackFrame sf = ti.getTopFrame();
		Abstraction abs_v1 = (Abstraction) sf.getOperandAttr(1);
		Abstraction abs_v2 = (Abstraction) sf.getOperandAttr(3);

		if (abs_v1 == null && abs_v2 == null)
			return super.execute(ti);
		else {
			long v1 = sf.peekLong(0);
			long v2 = sf.peekLong(2);

			Abstraction result = Abstraction._div(v1, abs_v1, v2, abs_v2);
			System.out.printf("LDIV> Values: %d (%s), %d (%s)\n", v2, abs_v2, v1, abs_v1);

			if (result.isComposite()) {
				ChoiceGenerator<?> cg;
				if (!ti.isFirstStepInsn()) { // first time around
					int size = result.getTokensNumber();
					cg = new FocusAbstractChoiceGenerator(size);
					ss.setNextChoiceGenerator(cg);
					return this;
				} else { // this is what really returns results
					cg = ss.getChoiceGenerator();
					assert (cg instanceof FocusAbstractChoiceGenerator);
					int key = (Integer) cg.getNextChoice();
					result = result.getToken(key);
					System.out.printf("LDIV> Result: %s\n", result);
				}
			} else
				System.out.printf("LDIV> Result: %s\n", result);

			sf.popLong();
			sf.popLong();
			
			sf.pushLong(0);
			sf = ti.getTopFrame();
			sf.setLongOperandAttr(result);

			return getNext(ti);
		}
	}

}
