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

import gov.nasa.jpf.vm.KernelState;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.jvm.bytecode.InstructionVisitor;

/**
 * abstraction for all comparison instructions
 */
public abstract class IfInstruction extends Instruction {

	protected int targetPosition; // insn position at jump insnIndex
	protected Instruction target; // jump target

	protected boolean conditionValue; // value of last evaluation of branch condition

	protected IfInstruction(int targetPosition) {
		this.targetPosition = targetPosition;
	}

	/**
	 * return which branch was taken. Only useful after instruction got executed
	 * WATCH OUT - 'true' means the jump condition is met, which logically is
	 * the 'false' branch
	 */
	public boolean getConditionValue() {
		return conditionValue;
	}

	/**
	 * Added so that SimpleIdleFilter can detect do-while loops when the while
	 * statement evaluates to true.
	 */
	public boolean isBackJump() {
		return (conditionValue) && (targetPosition <= position);
	}

	public Instruction getTarget() {
		if (target == null) {
			target = mi.getInstructionAt(targetPosition);
		}
		return target;
	}

	abstract public Instruction execute(SystemState ss, KernelState ks,
			ThreadInfo ti);

	public String toString() {
		return getMnemonic() + " " + targetPosition;
	}

	public int getLength() {
		return 3; // usually opcode, bb1, bb2
	}

	public void accept(InstructionVisitor insVisitor) {
		insVisitor.visit(this);
	}
}
