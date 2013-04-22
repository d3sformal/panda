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

import gov.nasa.jpf.JPFException;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.jvm.bytecode.InstructionVisitor;

/**
 * Access jump table by index and jump
 *   ..., index  => ...
 * WARNING: it actually duplicates LOOKUPSWITCH behavior
 */
public class TABLESWITCH extends SwitchInstruction implements
		gov.nasa.jpf.vm.TableSwitchInstruction {

	int min, max;

	public TABLESWITCH(int defaultTarget, int min, int max) {
		super(defaultTarget, (max - min + 1));
		this.min = min;
		this.max = max;
	}

	@Override
	public void setTarget(int value, int target) {
		int i = value - min;

		if (i >= 0 && i < targets.length) {
			targets[i] = target;
		} else {
			throw new JPFException("illegal tableswitch target: " + value);
		}
	}

	@Override
	protected Instruction executeConditional(ThreadInfo ti) {
		throw new UnsupportedOperationException(); // makes no sense with abstractions
	}

	@Override
	public Instruction execute(ThreadInfo ti) {
		/* TODO:
		 * TABLESWITCH is not implemented properly because it loses sense with
		 * abstractions. Instead LOOKUPSWITCH behavior is used. 
		 */
		return super.execute(ti);
	}

	@Override
	public int getLength() {
		return 13 + 2 * (matches.length);
	}

	@Override
	public int getByteCode() {
		return 0xAA;
	}

	@Override
	public void accept(InstructionVisitor insVisitor) {
		insVisitor.visit(this);
	}

}
