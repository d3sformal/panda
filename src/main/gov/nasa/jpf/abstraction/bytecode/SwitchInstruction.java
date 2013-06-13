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

import java.util.ArrayList;

import gov.nasa.jpf.abstraction.numeric.AbstractBoolean;
import gov.nasa.jpf.abstraction.numeric.AbstractValue;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.choice.IntChoiceFromList;

/**
 * common root class for LOOKUPSWITCH and TABLESWITCH insns
 *
 */
public abstract class SwitchInstruction extends
		gov.nasa.jpf.jvm.bytecode.SwitchInstruction {

	protected SwitchInstruction(int defaultTarget, int numberOfTargets) {
		super(defaultTarget, numberOfTargets);
	}

	@Override
	public Instruction execute(ThreadInfo ti) {
		
		SystemState ss = ti.getVM().getSystemState();
		StackFrame sf = ti.getModifiableTopFrame();
		AbstractValue abs_v = (AbstractValue) sf.getOperandAttr(0);

		if (abs_v == null) {
			return super.execute(ti);
		} else if (!ti.isFirstStepInsn()) {
			lastIdx = DEFAULT;
			int value = sf.peek(0);
			System.out.printf("Switch> Value: %d (%s)\n", value, abs_v);

			ArrayList<Integer> choices = new ArrayList<Integer>();
			for (int i = 0, l = matches.length; i < l; i++) {
				AbstractBoolean result = AbstractValue._eq(value, abs_v,
						matches[i], null);
				System.out.printf("Switch> Check %d -- %s\n", matches[i],
						result);
				if (result != AbstractBoolean.FALSE) {
					choices.add(i);
// Uncomment to jump after the first exact match like the switch operator
// does with concrete values 					
//					if (result == AbstractBoolean.TRUE)
//						break;
				}
			}
			
			if (choices.size() > 0) {
				int[] param = new int[choices.size()];
				for (int i = 0; i < choices.size(); ++i)
					param[i] = choices.get(i);
				ChoiceGenerator<?> cg = new IntChoiceFromList("abstractSwitchAll", param);
				ss.setNextChoiceGenerator(cg);
				return this;
			} else {
				sf.pop();
				System.out.printf("Switch> Result: default\n");
				return mi.getInstructionAt(target);
			}
		} else {
			ChoiceGenerator<?> cg = ss.getCurrentChoiceGenerator("abstractSwitchAll",
					IntChoiceFromList.class);
			int idx = ((IntChoiceFromList) cg).getNextChoice();
			sf.pop();

			System.out.printf("Switch> Result: choice #%d\n", idx);
			if (idx == -1) {
				return mi.getInstructionAt(target);
			}

			lastIdx = idx;
			
			return mi.getInstructionAt(targets[idx]);
		}
	}

}