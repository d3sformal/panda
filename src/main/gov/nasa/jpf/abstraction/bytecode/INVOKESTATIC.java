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

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class INVOKESTATIC extends gov.nasa.jpf.jvm.bytecode.INVOKESTATIC {

	public INVOKESTATIC(String clsName, String methodName, String methodSignature) {
		super(clsName, methodName, methodSignature);
	}
	
	@Override
	public Instruction execute(ThreadInfo ti) {

		Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);
		StackFrame before = ti.getTopFrame();

		Instruction actualNextInsn = super.execute(ti);
		
		StackFrame after = ti.getTopFrame();
		
		if (JPFInstructionAdaptor.testInvokeStaticInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
			return actualNextInsn;
		}
		
		for (int i = 0; i < after.getMethodInfo().getNumberOfStackArguments(); ++i) {
			Attribute attr = (Attribute) after.getOperandAttr(i);
			
			if (attr == null) attr = new EmptyAttribute();
			
			after.getMethodInfo().addAttr(attr);
		}
				
		GlobalAbstraction.getInstance().processMethodCall(ti, before, after);

		return actualNextInsn;
	}
}
