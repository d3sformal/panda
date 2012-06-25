//Copyright (C) 2007 United States Government as represented by the
//Administrator of the National Aeronautics and Space Administration
//(NASA).  All Rights Reserved.

//This software is distributed under the NASA Open Source Agreement
//(NOSA), version 1.3.  The NOSA has been approved by the Open Source
//Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
//directory tree for the complete NOSA document.

//THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
//KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
//LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
//SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
//A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
//THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
//DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.abstraction.numeric.FocusAbstractChoiceGenerator;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.StackFrame;



public class IADD extends gov.nasa.jpf.jvm.bytecode.IADD {

	@Override
	public Instruction execute (SystemState ss, KernelState ks, ThreadInfo th) {

		StackFrame sf; 
		sf = th.getTopFrame();
		
		Abstraction abs_v1 = (Abstraction) sf.getOperandAttr(0);
		Abstraction abs_v2 = (Abstraction) sf.getOperandAttr(1);
		if(abs_v1==null && abs_v2==null)
			return super.execute(ss, ks, th);
		else {
			
			int v1 = th.peek(0);
			int v2 = th.peek(1);
			
			Abstraction result = Abstraction._add(v1, abs_v1, v2, abs_v2);
			System.out.println("values "+v1 + " " + v2 + " "+ abs_v1 + " "+abs_v2+ " result "+result);
			// TODO: what happens if we have multiple abstractions; do they interfere?
			
			// here we create a new focus choice generator 
			// and set the result non-deterministically to each 
			// token in the abstract domain
			if(result.isTop()) {
				ChoiceGenerator<?> cg;
				if (!th.isFirstStepInsn()) { // first time around
					int size = result.get_num_tokens();
					cg = new FocusAbstractChoiceGenerator(size);
					ss.setNextChoiceGenerator(cg);
					return this;
				}
				else { // this is what really returns results
					cg = ss.getChoiceGenerator();
					assert (cg instanceof FocusAbstractChoiceGenerator);
					int key = (Integer) cg.getNextChoice();
					result = result.get_token(key);
					System.out.println("result ..."+result);
				}
			}

			
			//sf = th.getTopFrame();
			th.pop();
			th.pop();
			
			th.push(0, false);
			sf = th.getTopFrame();
			sf.setOperandAttr(result);

			System.out.println("Execute IADD: "+result);

			return getNext(th);
		}
	}
	
}
