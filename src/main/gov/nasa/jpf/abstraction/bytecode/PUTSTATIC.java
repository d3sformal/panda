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

import java.util.Collection;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.predicate.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.predicate.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.predicate.grammar.AccessPath;
import gov.nasa.jpf.abstraction.predicate.state.ScopedSymbolTable;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class PUTSTATIC extends gov.nasa.jpf.jvm.bytecode.PUTSTATIC {
	
	public PUTSTATIC(String fieldName, String classType, String fieldDescriptor) {
		super(fieldName, classType, fieldDescriptor);
	}

	@Override
	public Instruction execute(ThreadInfo ti) {        
		StackFrame sf = ti.getModifiableTopFrame();
		
        Attribute source = (Attribute) sf.getOperandAttr();
        ConcretePath pathRoot = new ConcretePath(getClassName(), ti, ti.getClassInfo().getStaticElementInfo(), ConcretePath.Type.STATIC);
        pathRoot.appendSubElement(getFieldName());

		Instruction ret = super.execute(ti);

        if (source == null) {
        	Collection<CompleteVariableID> numbers = pathRoot.resolve().values();
			
			if (!numbers.isEmpty()) {
				ScopedSymbolTable.getInstance().registerPathToVariable(pathRoot, numbers.iterator().next());
			}
        } else {
            ConcretePath prefix = source.accessPath;

            if (prefix != null) {
                for (AccessPath path : ScopedSymbolTable.getInstance().lookupAccessPaths(prefix)) {
            	    CompleteVariableID variableID = ScopedSymbolTable.getInstance().resolvePath(path);

		    		ConcretePath newPath = (ConcretePath) path.clone();
                    ConcretePath newPathRoot = (ConcretePath) pathRoot.clone();

                    AccessPath.reRoot(newPath, prefix, newPathRoot);

		            ScopedSymbolTable.getInstance().registerPathToVariable(newPath, variableID);
			    }
            }
        }

		return ret;
	}
}
