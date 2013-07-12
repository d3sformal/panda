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

import gov.nasa.jpf.abstraction.AbstractInstructionFactory;
import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.FocusAbstractChoiceGenerator;
import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.abstraction.impl.NonEmptyAttribute;
import gov.nasa.jpf.abstraction.predicate.common.Add;
import gov.nasa.jpf.abstraction.predicate.common.Constant;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;

/**
 * Increment local variable by constant
 * No change
 */
public class IINC extends gov.nasa.jpf.jvm.bytecode.IINC {

	public IINC(int localVarIndex, int increment) {
		super(localVarIndex, increment);
	}

	public Instruction execute(ThreadInfo ti) {

		SystemState ss = ti.getVM().getSystemState();
		StackFrame sf = ti.getModifiableTopFrame();
		LocalVarInfo var = sf.getLocalVarInfo(index);
		Attribute attr = (Attribute) sf.getLocalAttr(index);
		
		if (attr == null) attr = new EmptyAttribute();
		
		AbstractValue abs_v = attr.getAbstractValue();
		
		Expression expression = attr.getExpression();

		if (expression == null) expression = new ConcretePath(sf.getLocalVarInfo(index).getName(), ti, var, ConcretePath.Type.LOCAL);

		expression = new Add(expression, new Constant(increment));
		
		if (abs_v == null) {
			Attribute result = new NonEmptyAttribute(null, expression);

			sf.setLocalAttr(index, result);
			sf.setLocalVariable(index, sf.getLocalVariable(index) + increment, false);
		} else {
			Attribute result = new NonEmptyAttribute(Abstraction._add(0, abs_v, increment, null), expression);

			System.out.printf("IINC> Value:  %d (%s)\n", sf.getLocalVariable(index), abs_v);

			if (result.getAbstractValue().isComposite()) {
				System.out.println("Top");

				if (!ti.isFirstStepInsn()) { // first time around
					int size = result.getAbstractValue().getTokensNumber();
					
					System.out.println("size "+size);//should be 3
					
					ChoiceGenerator<?> cg = new FocusAbstractChoiceGenerator(size);
					ss.setNextChoiceGenerator(cg);
					
					return this;
				} else { // this is what really returns results
					ChoiceGenerator<?> cg = ss.getChoiceGenerator();
					
					assert (cg instanceof FocusAbstractChoiceGenerator);
					
					int key = (Integer) cg.getNextChoice();
					result.setAbstractValue(result.getAbstractValue().getToken(key));
				}
			}
			
			System.out.printf("IINC> Result: %s\n", result);
			
			sf.setLocalAttr(index, result);
			sf.setLocalVariable(index, 0, false);
		}
		
		AbstractInstructionFactory.abs.processStore(expression, new ConcretePath(sf.getLocalVarInfo(index).getName(), ti, var, ConcretePath.Type.LOCAL));

		return getNext(ti);
	}

}
