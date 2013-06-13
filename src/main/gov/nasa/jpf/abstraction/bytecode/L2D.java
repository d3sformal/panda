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

import gov.nasa.jpf.abstraction.numeric.AbstractValue;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;

/**
 * Convert long to double
 * ..., value => ..., result
 */
public class L2D extends gov.nasa.jpf.jvm.bytecode.L2D {

	public Instruction execute(ThreadInfo ti) {

		StackFrame sf = ti.getModifiableTopFrame();
		AbstractValue abs_val = (AbstractValue) sf.getLongOperandAttr();

		if (abs_val == null) {
			return super.execute(ti);
		}

		long val = sf.popLong(); // just to pop it

		sf.pushLong(0);
		sf.setLongOperandAttr(abs_val);

		System.out.printf("L2D> Values: %d (%s)\n", val, abs_val);
		System.out.printf("L2D> Result: %s\n", sf.getLongOperandAttr());

		return getNext(ti);

	}

}