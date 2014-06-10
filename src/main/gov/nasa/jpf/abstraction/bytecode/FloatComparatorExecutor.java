//
// Copyright (C) 2012 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All RightHandSides Reserved.
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

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.vm.StackFrame;

/**
 * Implements type specific parts of the comparison ( @see gov.nasa.jpf.abstraction.bytecode.BinaryComparatorExecutor )
 *   - layout of operands (and their attributes) on the stack (type size comes into play)
 */
public class FloatComparatorExecutor extends BinaryComparatorExecutor<Float> {

    private static FloatComparatorExecutor instance;

    public static FloatComparatorExecutor getInstance() {
        if (instance == null) {
            instance = new FloatComparatorExecutor();
        }

        return instance;
    }

    @Override
    protected Expression getLeftHandSideExpression(StackFrame sf) {
        return getExpression(sf, 0);
    }

    @Override
    protected Expression getRightHandSideExpression(StackFrame sf) {
        return getExpression(sf, 1);
    }

    @Override
    final protected Float getLeftHandSideOperand(StackFrame sf) {
        return sf.peekFloat(0);
    }

    @Override
    final protected Float getRightHandSideOperand(StackFrame sf) {
        return sf.peekFloat(1);
    }

    @Override
    final protected void storeResult(Expression result, StackFrame sf) {
        sf.popFloat();
        sf.popFloat();

        sf.push(((Constant) result).value.intValue());
        sf.setOperandAttr(result);
    }

}
