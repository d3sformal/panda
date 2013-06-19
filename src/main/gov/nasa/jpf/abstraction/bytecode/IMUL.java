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
import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Multiply integer
 * ..., value1, value2 => ..., result
 */
public class IMUL extends gov.nasa.jpf.jvm.bytecode.IMUL implements AbstractBinaryOperator<Integer> {

	IntegerBinaryOperatorExecutor executor = IntegerBinaryOperatorExecutor.getInstance();
	
	@Override
	public Instruction execute(ThreadInfo ti) {
		
		/**
		 * Delegates the call to a shared object that does all the heavy lifting
		 */
		return executor.execute(this, ti);
	}

	@Override
	public AbstractValue getResult(Integer v1, AbstractValue abs_v1, Integer v2, AbstractValue abs_v2) {
		
		/**
		 * Performs the adequate operation over abstractions
		 */
		return Abstraction._mul(v1, abs_v1, v2, abs_v2);
	}

	@Override
	public Instruction executeConcrete(ThreadInfo ti) {
		
		/**
		 * Ensures execution of the original instruction
		 */
		return super.execute(ti);
	}

	@Override
	public Instruction getSelf() {
		
		/**
		 * Ensures translation into an ordinary instruction
		 */
		return this;
	}

}