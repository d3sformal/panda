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

import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.NativeStackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;

import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.abstraction.impl.NonEmptyAttribute;
import gov.nasa.jpf.abstraction.predicate.state.MethodFrameSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;

public class NATIVERETURN extends gov.nasa.jpf.jvm.bytecode.NATIVERETURN {
	
	@Override
	public Instruction execute(ThreadInfo ti) {

		Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);
		NativeStackFrame before = (NativeStackFrame) ti.getModifiableTopFrame();
        Object retValue = before.getReturnValue();

        // Push the value onto the stack
        // no matter it is native, we need to have an attribute on the stack to return it in processMethodReturn
        switch (before.getMethodInfo().getReturnTypeCode()) {
            case Types.T_ARRAY:
            case Types.T_REFERENCE:
                int ref = ((Integer) retValue).intValue();

                AnonymousObject returnValue = AnonymousObject.create(new Reference(ti.getElementInfo(ref)));
                
                before.setReturnAttr(new NonEmptyAttribute(null, returnValue));
                break;

            case Types.T_BOOLEAN:
            case Types.T_BYTE:
            case Types.T_CHAR:
            case Types.T_SHORT:
            case Types.T_INT:
            case Types.T_FLOAT:
            case Types.T_LONG:
            case Types.T_DOUBLE:
                before.setReturnAttr(new NonEmptyAttribute(null, MethodFrameSymbolTable.DUMMY_VARIABLE));
                break;
            
            case Types.T_VOID:
            default:
        }
		
		Instruction actualNextInsn = super.execute(ti);

		StackFrame after = ti.getTopFrame();
		
		if (JPFInstructionAdaptor.testReturnInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
			return actualNextInsn;
		}
		
        if (before.getMethodInfo().getReturnTypeCode() == Types.T_VOID) {
    		GlobalAbstraction.getInstance().processVoidMethodReturn(ti, before, after);
        } else {
    		GlobalAbstraction.getInstance().processMethodReturn(ti, before, after);
        }

		return actualNextInsn;
	}
}
