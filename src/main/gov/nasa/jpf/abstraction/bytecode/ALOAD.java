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
import gov.nasa.jpf.abstraction.predicate.common.ConcretePath;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class ALOAD extends gov.nasa.jpf.jvm.bytecode.ALOAD {

	public ALOAD(int index) {
		super(index);
	}
	
	@Override
	public Instruction execute(ThreadInfo ti) {
		StackFrame sf = ti.getModifiableTopFrame();
		LocalVarInfo var = getLocalVarInfo();
		
		String v1 = null;
		String v2 = null;
		String v3 = null;
		
		try { v1 = getLocalVarInfo().getName(); } catch (Exception e) {}
		try { v2 = sf.getLocalVarInfo(index).getName(); } catch (Exception e) {}
		try { v3 = getMethodInfo().getLocalVars()[index].getName(); } catch (Exception e) {}
		
		System.err.println("L " + ((v1 != null && v2 != null && v3 != null && v1.equals(v2) && v2.equals(v3)) || (v1 == v2 && v2 == v3 && v1 == null) ? "OK" : "EE") + " " + v1 + " " + v2 + " " + v3);
		
		Instruction ret = super.execute(ti);
	
        if (var != null) {	
    		ElementInfo ei = ti.getElementInfo(sf.getLocalVariable(index));
	    	ConcretePath path = new ConcretePath(var.getName(), ti, ei, ConcretePath.Type.HEAP);
			
    		if (ei != null) {
	    		Attribute attribute = new Attribute(null, path);

		    	sf.setOperandAttr(attribute);
    		}
        }

		return ret;
	}
}
