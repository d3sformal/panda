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
import gov.nasa.jpf.abstraction.numeric.Signs;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

/**
 * Compare long ..., value1, value2 => ..., result
 */
public class LCMP extends gov.nasa.jpf.jvm.bytecode.LCMP {

	@Override
	public Instruction execute(SystemState ss, KernelState ks, ThreadInfo th) {
		StackFrame sf = th.getTopFrame();

		Abstraction abs_v1 = (Abstraction) sf.getOperandAttr(1);
		Abstraction abs_v2 = (Abstraction) sf.getOperandAttr(3);
		
		if(abs_v1==null && abs_v2==null)
			return super.execute(ss, ks, th);
		else {
			long v1 = th.longPeek(0);
			long v2 = th.longPeek(2);

			// abs_v2 compare to abs_v1
			Abstraction result = Abstraction._cmp(v1, abs_v1, v2, abs_v2);
			System.out.printf("LCMP> Values: %d (%s), %d (%s)\n", v2, abs_v2, v1, abs_v1);
			if (result.isTop()) {
				ChoiceGenerator<?> cg;
				if (!th.isFirstStepInsn()) { // first time around
					int size = result.get_num_tokens();
					cg = new FocusAbstractChoiceGenerator(size);
					ss.setNextChoiceGenerator(cg);
					return this;
				} else { // this is what really returns results
					cg = ss.getChoiceGenerator();
					assert (cg instanceof FocusAbstractChoiceGenerator);
					int key = (Integer) cg.getNextChoice();
					result = result.get_token(key);
					System.out.printf("LCMP> Result: %s\n", result);
				}
			} else
				System.out.printf("LFCMP> Result: %s\n", result);

			th.longPop();
			th.longPop();
			
			Signs s_result = (Signs)result;
			if (s_result == Signs.NEG)
				th.push(-1, false);
			else if (s_result == Signs.POS)
				th.push(+1, false);
			else
				th.push(0, false);
			
			sf = th.getTopFrame();
			sf.setOperandAttr(result); // redundant

			return getNext(th);
		}	
	}

	protected int conditionValue(long v1, long v2) {
		if (v1 == v2) {
			return 0;
		} else if (v2 > v1) {
			return 1;
		} else {
			return -1;
		}
	}

}
