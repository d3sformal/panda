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
import gov.nasa.jpf.abstraction.predicate.concrete.ConcretePath;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class AALOAD extends gov.nasa.jpf.jvm.bytecode.AALOAD {
	
	@Override
	public Instruction execute(ThreadInfo ti) {
		System.err.println("AALOAD");

		StackFrame sf = ti.getModifiableTopFrame();
		
		Instruction ret = super.execute(ti);
		
		Attribute attr1 = (Attribute) sf.getOperandAttr(1);
		Attribute attr2 = (Attribute) sf.getOperandAttr(0);
		
		if (attr1 != null) {
			ConcretePath path1 = attr1.accessPath;

			if (path1 != null && attr2 != null) {
				ConcretePath path2 = attr2.accessPath;
				
				path1.appendIndexElement(path2);
				
				Attribute attribute = new Attribute(null, path1);
						
				sf.setOperandAttr(attribute);
			}
		}

		return ret;
	}
}