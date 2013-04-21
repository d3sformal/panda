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
import gov.nasa.jpf.vm.Types;
import gov.nasa.jpf.vm.Instruction;

/**
 * Negate float
 * ..., value => ..., result
 */
public class FNEG extends gov.nasa.jpf.jvm.bytecode.FNEG {

	@Override
	public Instruction execute(ThreadInfo ti) {

		SystemState ss = ti.getVM().getSystemState();
		StackFrame sf = ti.getTopFrame();
		Abstraction abs_val = (Abstraction) sf.getOperandAttr(0);
		if (abs_val == null)
			return super.execute(ti);
		else {
			float val = Types.intToFloat(sf.pop()); // just to pop it

			Abstraction result = Abstraction._neg(abs_val);
			System.out.printf("FNEG> Values: %f (%s)\n", val, abs_val);
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
					System.out.printf("FNEG> Result: %s\n", result);
				}
			} else
				System.out.printf("FNEG> Result: %s\n", result);

			sf.push(0, false);
			sf.setOperandAttr(result);

			System.out.println("Execute FNEG: " + result);

			return getNext(ti);
		}
	}	
	
}
