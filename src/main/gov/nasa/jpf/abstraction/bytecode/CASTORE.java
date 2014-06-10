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

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.util.ExpressionUtil;
import gov.nasa.jpf.jvm.bytecode.ArrayElementInstruction;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Stores a short value into an array
 */
public class CASTORE extends gov.nasa.jpf.jvm.bytecode.CASTORE implements ArrayStoreInstruction {

    private ArrayStoreExecutor executor = new ArrayStoreExecutor();

    @Override
    public Instruction execute(ThreadInfo ti) {
        return executor.execute(this, ti);
    }

    @Override
    public Instruction executeConcrete(ThreadInfo ti) {
        return super.execute(ti);
    }

    @Override
    public AccessExpression getArrayExpression(StackFrame sf) {
        return ExpressionUtil.getAccessExpression(sf.getOperandAttr(2));
    }

    @Override
    public ElementInfo getArray(StackFrame sf) {
        ThreadInfo ti = ThreadInfo.getCurrentThread();

        return ti.getElementInfo(sf.peek(2));
    }

    @Override
    public Expression getSourceExpression(StackFrame sf) {
        return ExpressionUtil.getExpression(sf.getOperandAttr(0));
    }

    @Override
    public Expression getIndexExpression(StackFrame sf) {
        return ExpressionUtil.getExpression(sf.getOperandAttr(1));
    }

    @Override
    public ArrayElementInstruction getSelf() {
        return this;
    }
}
