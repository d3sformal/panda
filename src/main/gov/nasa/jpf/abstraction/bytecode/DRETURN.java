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

import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Bytecode instruction DRETURN
 * ... => double
 */
public class DRETURN extends gov.nasa.jpf.jvm.bytecode.DRETURN {
	
	@Override
	public Instruction execute(ThreadInfo ti) {

        /**
         * Find out what is expected to follow
         */
		Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);
		StackFrame before = ti.getTopFrame();

		Instruction actualNextInsn = super.execute(ti);
		
		StackFrame after = ti.getTopFrame();
		
        /**
         * Test whether the instruction was successfully executed (a choice may have been generated)
         */
		if (JPFInstructionAdaptor.testReturnInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
			return actualNextInsn;
		}
		
        /**
         * Inform abstractions about this event
         */
        GlobalAbstraction.getInstance().processMethodReturn(ti, before, after);

		return actualNextInsn;
	}
}
