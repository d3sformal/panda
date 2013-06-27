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
import gov.nasa.jpf.abstraction.numeric.SignsAbstraction;
import gov.nasa.jpf.abstraction.numeric.SignsValue;
import gov.nasa.jpf.vm.StackFrame;

public class DoubleComparatorExecutor extends BinaryOperatorExecutor<Double> {

	private static DoubleComparatorExecutor instance;

	public static DoubleComparatorExecutor getInstance() {
		if (instance == null) {
			instance = new DoubleComparatorExecutor();
		}
		
		return instance;
	}

	@Override
	protected AbstractValue getLeftAbstractValue(StackFrame sf) {
		return getAbstractValue(sf, 1);
	}

	@Override
	protected AbstractValue getRightAbstractValue(StackFrame sf) {
		return getAbstractValue(sf, 3);
	}

	@Override
	final protected Double getLeftOperand(StackFrame sf) {
		return sf.peekDouble(0);
	}

	@Override
	final protected Double getRightOperand(StackFrame sf) {
		return sf.peekDouble(2);
	}

	@Override
	final protected void storeResult(AbstractValue result, StackFrame sf) {
		sf.popDouble();
		sf.popDouble();
		
		SignsValue s_result = (SignsValue) result;

		if (s_result == SignsAbstraction.NEG) {
			sf.push(-1);
		} else if (s_result == SignsAbstraction.POS) {
			sf.push(+1);
		} else {
			sf.push(0);
		}
	}

}
