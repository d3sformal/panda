//Copyright (C) 2012 United States Government as represented by the
//Administrator of the National Aeronautics and Space Administration
//(NASA).  All Rights Reserved.

//This software is distributed under the NASA Open Source Agreement
//(NOSA), version 1.3.  The NOSA has been approved by the Open Source
//Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
//directory tree for the complete NOSA document.

//THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
//KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
//LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
//SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
//A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
//THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
//DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.abstraction.numeric.FocusAbstractChoiceGenerator;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;

/**
 * Subtract int
 * ..., value1, value2 => ..., result
 */
public class ISUB extends gov.nasa.jpf.jvm.bytecode.ISUB {

	@Override
	public Instruction execute(ThreadInfo ti) {

		SystemState ss = ti.getVM().getSystemState();
		StackFrame sf = ti.getTopFrame();

		Abstraction abs_v1 = (Abstraction) sf.getOperandAttr(0);
		Abstraction abs_v2 = (Abstraction) sf.getOperandAttr(1);

		if (abs_v1 == null && abs_v2 == null) {
			return super.execute(ti);
		}

		int v1 = sf.peek(0);
		int v2 = sf.peek(1);

		// abs_v2 - abs_v1
		Abstraction result = Abstraction._sub(v1, abs_v1, v2, abs_v2);

		System.out.printf("ISUB> Values: %d (%s), %d (%s)\n", v2, abs_v2, v1, abs_v1);

		if (result.isComposite()) {
			if (!ti.isFirstStepInsn()) { // first time around
				int size = result.getTokensNumber();
				ChoiceGenerator<?> cg;cg = new FocusAbstractChoiceGenerator(size);
				ss.setNextChoiceGenerator(cg);
				
				return this;
			} else { // this is what really returns results
				ChoiceGenerator<?> cg;cg = ss.getChoiceGenerator();
					
				assert (cg instanceof FocusAbstractChoiceGenerator);
				
				int key = (Integer) cg.getNextChoice();
				result = result.getToken(key);
			}
		}
		
		System.out.printf("ISUB> Result: %s\n", result);

		sf.pop();
		sf.pop();

		sf.push(0, false);
		sf.setOperandAttr(result);

		return getNext(ti);
	}

}