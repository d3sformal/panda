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
import gov.nasa.jpf.abstraction.util.ExpressionUtil;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Implementation of unary operations regardless their concrete type.
 */
public abstract class UnaryOperatorExecutor<T> {

    final public Instruction execute(AbstractUnaryOperator<T> op, ThreadInfo ti) {
        StackFrame sf = ti.getModifiableTopFrame();

        Expression expr = getExpression(sf);
        Expression result = op.getResult(expr);

        Instruction ret = op.executeConcrete(ti);

        storeExpression(result, sf);

        return ret;
    }

    protected Expression getExpression(StackFrame sf, int index) {
        return ExpressionUtil.getExpression(sf.getOperandAttr(index));
    }

    abstract protected Expression getExpression(StackFrame sf);
    abstract protected T getOperand(StackFrame sf);
    abstract protected void storeExpression(Expression result, StackFrame sf);
    abstract protected void storeResult(Expression result, StackFrame sf);
}
