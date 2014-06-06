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
import gov.nasa.jpf.vm.StackFrame;

/**
 * Implements type specific parts of the operation ( @see gov.nasa.jpf.abstraction.bytecode.UnaryOperatorExecutor )
 *   - layout of the stack (type size comes into play)
 */
public class IntegerUnaryOperatorExecutor extends UnaryOperatorExecutor<Integer> {

    private static IntegerUnaryOperatorExecutor instance;

    public static IntegerUnaryOperatorExecutor getInstance() {
        if (instance == null) {
            instance = new IntegerUnaryOperatorExecutor();
        }

        return instance;
    }

    @Override
    protected Expression getExpression(StackFrame sf) {
        return getExpression(sf, 0);
    }


    @Override
    final protected Integer getOperand(StackFrame sf) {
        return sf.peek(0);
    }

    @Override
    final protected void storeExpression(Expression result, StackFrame sf) {
        sf.setOperandAttr(result);
    }

    @Override
    final protected void storeResult(Expression result, StackFrame sf) {
        sf.pop();

        sf.push(0);
        storeExpression(result, sf);
    }

}
