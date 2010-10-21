//
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

import gov.nasa.jpf.abstraction.numeric.AbstractBoolean;
import gov.nasa.jpf.abstraction.numeric.AbstractChoiceGenerator;
import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.StackFrame;



public class ISUB extends gov.nasa.jpf.jvm.bytecode.ISUB {

	@Override
	public Instruction execute (SystemState ss, KernelState ks, ThreadInfo th) {

		StackFrame sf = th.getTopFrame();
		Abstraction abs_v1 = (Abstraction) sf.getOperandAttr(0);
		Abstraction abs_v2 = (Abstraction) sf.getOperandAttr(1);


		if(abs_v1==null && abs_v2==null)
			return super.execute(ss, ks, th); // we'll still do the concrete execution
		else {
			int v1 = th.pop();
			int v2 = th.pop();
			th.push(0, false); // for abstract expressions, the concrete value does not matter for now

			Abstraction result = null;
			if(abs_v2!=null) {
				if (abs_v1!=null)
					result = abs_v2._minus(abs_v1);
				else // v1 is concrete
					result = abs_v2._minus(v1);
			}
			else if (abs_v1!=null)
				result = abs_v1._minus_reverse(v2);
			sf.setOperandAttr(result);

			//System.out.println("Execute ISUB: "+result);

			return getNext(th);
		}
	}

}
