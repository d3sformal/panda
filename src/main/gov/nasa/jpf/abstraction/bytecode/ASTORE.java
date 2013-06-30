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
import gov.nasa.jpf.abstraction.predicate.common.AccessPath;
import gov.nasa.jpf.abstraction.predicate.common.ConcretePath;
import gov.nasa.jpf.abstraction.predicate.common.ScopedSymbolTable;
import gov.nasa.jpf.abstraction.predicate.common.VariableID;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class ASTORE extends gov.nasa.jpf.jvm.bytecode.ASTORE {

	public ASTORE(int index) {
		super(index);
	}
	
	@Override
	public Instruction execute(ThreadInfo ti) {
		//TODO
		/*
		 * current objRef may have an access path in the attribute
		 * we need to find all paths in Symbol table whose prefix it is
		 * 
		 * those need to be copied
		 * copies need to be rerooted to start in this local variable
		 * new paths need to be registered in Symbol table
		 */
		StackFrame sf = ti.getModifiableTopFrame();
		LocalVarInfo var = getMethodInfo().getLocalVars()[index];

		ElementInfo ei = ti.getElementInfo(sf.getLocalVariable(index));

		Attribute attribute = (Attribute) sf.getOperandAttr();
		
		if (attribute != null) {
			ConcretePath prefix = attribute.accessPath;

            //System.err.println("ASTORE " + var.getName() + " := " + prefix);
				
			for (AccessPath path : ScopedSymbolTable.getInstance().lookupAccessPaths(prefix)) {
				VariableID variableID = ScopedSymbolTable.getInstance().resolvePath(path);

                //System.err.println("\t" + path);

				ConcretePath clone = (ConcretePath) path.clone();
			
				ConcretePath.reRoot(clone, prefix, var.getName(), ti, ei, ConcretePath.Type.HEAP);

				ScopedSymbolTable.getInstance().register(clone, variableID);
			}
		}
		
		return super.execute(ti);
	}
}
