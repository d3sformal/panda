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

import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.vm.StackFrame;

public class LongBinaryOperatorExecutor extends BinaryOperatorExecutor<Long> {

	private static LongBinaryOperatorExecutor instance;

	public static LongBinaryOperatorExecutor getInstance() {
		if (instance == null) {
			instance = new LongBinaryOperatorExecutor();
		}
		
		return instance;
	}
	
	@Override
	protected AbstractValue getLeftAbstractValue(StackFrame sf) {
		return (AbstractValue)sf.getOperandAttr(1);
	}

	@Override
	protected AbstractValue getRightAbstractValue(StackFrame sf) {
		return (AbstractValue)sf.getOperandAttr(3);
	}

	@Override
	protected Long getLeftOperand(StackFrame sf) {
		return sf.peekLong(0);
	}

	@Override
	protected Long getRightOperand(StackFrame sf) {
		return sf.peekLong(2);
	}

	@Override
	protected void storeResult(AbstractValue result, StackFrame sf) {
		sf.popLong();
		sf.popLong();
		
		sf.pushLong(0);
		sf.setLongOperandAttr(result);
	}

}
