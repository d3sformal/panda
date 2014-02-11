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
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.impl.NonEmptyAttribute;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class ALOAD extends gov.nasa.jpf.jvm.bytecode.ALOAD {

	public ALOAD(int index) {
		super(index);
	}
	
	@Override
	public Instruction execute(ThreadInfo ti) {
		Instruction actualNextInsn = super.execute(ti);
		
        StackFrame sf = ti.getTopFrame();
	    AccessExpression path = DefaultRoot.create(getLocalVariableName(), getLocalVariableIndex());


        java.util.Set<gov.nasa.jpf.abstraction.predicate.state.universe.UniverseIdentifier> values = new java.util.HashSet<gov.nasa.jpf.abstraction.predicate.state.universe.UniverseIdentifier>();
        ((gov.nasa.jpf.abstraction.predicate.PredicateAbstraction) gov.nasa.jpf.abstraction.GlobalAbstraction.getInstance().get()).getSymbolTable().get(0).lookupValues(path, values);
        System.out.println("LOADED SYMBOL -> " + values);
			
	    Attribute attribute = new NonEmptyAttribute(null, path);
	    		
	    sf = ti.getModifiableTopFrame();
		sf.setOperandAttr(attribute);

		return actualNextInsn;
	}
}
