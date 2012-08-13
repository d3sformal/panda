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

import gov.nasa.jpf.JPFException;
import gov.nasa.jpf.abstraction.numeric.AbstractBoolean;
import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InstructionVisitor;
import gov.nasa.jpf.jvm.choice.IntChoiceFromList;
import gov.nasa.jpf.jvm.choice.IntIntervalGenerator;

/**
 * Access jump table by index and jump ..., index ...
 */
public class TABLESWITCH extends SwitchInstruction implements
		gov.nasa.jpf.jvm.TableSwitchInstruction {

	int min, max;

	public TABLESWITCH(int defaultTarget, int min, int max) {
		super(defaultTarget, (max - min + 1));
		this.min = min;
		this.max = max;
	}

	public void setTarget(int value, int target) {
		int i = value - min;

		if (i >= 0 && i < targets.length) {
			targets[i] = target;
		} else {
			throw new JPFException("illegal tableswitch target: " + value);
		}
	}

	protected Instruction executeConditional(SystemState ss, KernelState ks,
			ThreadInfo ti) {
		throw new UnsupportedOperationException();
		// makes no sense with abstractions
	}

	@Override
	public Instruction execute(SystemState ss, KernelState ks, ThreadInfo th) {
		StackFrame sf = th.getTopFrame();
		Abstraction abs_v = (Abstraction) sf.getOperandAttr(0);

		if (abs_v == null)
			return super.execute(ss, ks, th);
		else if (!th.isFirstStepInsn()) {
			lastIdx = DEFAULT;
			int value = th.peek(0);
			System.out.printf("Switch> Value: %d (%s)\n", value, abs_v);

			ArrayList<Integer> choices = new ArrayList<Integer>();
			for (int i = 0, l = matches.length; i < l; i++) {
				AbstractBoolean result = Abstraction._eq(value, abs_v,
						matches[i], null);
				System.out.printf("Switch> Check %d -- %s\n", matches[i],
						result);
				if (result != AbstractBoolean.FALSE) {
					choices.add(i);
					if (result == AbstractBoolean.TRUE)
						break; // remove this if not just first match should be processed
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
				th.pop();
				System.out.printf("Switch> Result: default\n");
				return mi.getInstructionAt(target);
			}
		} else {
			ChoiceGenerator<?> cg = ss.getCurrentChoiceGenerator("abstractSwitchAll",
					IntChoiceFromList.class);
			int idx = ((IntChoiceFromList) cg).getNextChoice();
			th.pop();

			System.out.printf("Switch> Result: choice #%d\n", idx);
			if (idx == -1)
				return mi.getInstructionAt(target);
			else {
				lastIdx = idx;
				return mi.getInstructionAt(targets[idx]);
			}

		}
	}

	public int getLength() {
		return 13 + 2 * (matches.length); // <2do> NOT RIGHT: padding!!
	}

	public int getByteCode() {
		return 0xAA;
	}

	public void accept(InstructionVisitor insVisitor) {
		insVisitor.visit(this);
	}

}
