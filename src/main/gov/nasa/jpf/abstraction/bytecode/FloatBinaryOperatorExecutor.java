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
import gov.nasa.jpf.vm.StackFrame;

public class FloatBinaryOperatorExecutor extends BinaryOperatorExecutor<Float> {

	private static FloatBinaryOperatorExecutor instance;

	public static FloatBinaryOperatorExecutor getInstance() {
		if (instance == null) {
			instance = new FloatBinaryOperatorExecutor();
		}
		
		return instance;
	}
	
	@Override
	protected Abstraction getLeftAbstraction(StackFrame sf) {
		return (Abstraction)sf.getOperandAttr(0);
	}

	@Override
	protected Abstraction getRightAbstraction(StackFrame sf) {
		return (Abstraction)sf.getOperandAttr(1);
	}
	
	@Override
	final protected Float getLeft(StackFrame sf) {
		return sf.peekFloat(0);
	}

	@Override
	final protected Float getRight(StackFrame sf) {
		return sf.peekFloat(1);
	}

	@Override
	final protected void cleanUp(StackFrame sf) {
		sf.popFloat();
		sf.popFloat();
		
		sf.pushFloat(0);
	}

}
