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
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayLengthRead;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ArrayIndexOutOfBoundsExecutiveException;
import gov.nasa.jpf.abstraction.util.RunDetector;

/**
 * Stores a short value into an array
 */
public class BASTORE extends gov.nasa.jpf.jvm.bytecode.BASTORE {

    private static final String ARRAY_INDEX_OUT_OF_BOUNDS = "java.lang.ArrayIndexOutOfBoundsException";

    @Override
    public Instruction execute(ThreadInfo ti) {
        StackFrame sf = ti.getTopFrame();
        Expression from = Attribute.getExpression(sf.getOperandAttr(0));
        Expression index = Attribute.getExpression(sf.getOperandAttr(1));
        AccessExpression to = Attribute.getAccessExpression(sf.getOperandAttr(2));
        AccessExpression element = DefaultArrayElementRead.create(to, index);
        AccessExpression length = DefaultArrayLengthRead.create(to);

        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);

        if (RunDetector.isRunning() && !RunDetector.isInLibrary(ti)) {
            Predicate inBounds = Conjunction.create(
                Negation.create(LessThan.create(index, Constant.create(0))),
                LessThan.create(index, length)
            );

            TruthValue value = (TruthValue) GlobalAbstraction.getInstance().processBranchingCondition(inBounds);

            if (value != TruthValue.TRUE) {
                throw new ArrayIndexOutOfBoundsExecutiveException(ThreadInfo.getCurrentThread().createAndThrowException(ARRAY_INDEX_OUT_OF_BOUNDS, "Cannot ensure: " + inBounds));
            }
        }

        // Here we may write into a different index than those corresponding to abstract state
        // Only if we do not apply pruning of infeasible paths (inconsistent concrete/abstract state)
        Instruction actualNextInsn = super.execute(ti);

        if (JPFInstructionAdaptor.testArrayElementInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        // Element indices are derived from predicates in this method call
        GlobalAbstraction.getInstance().processPrimitiveStore(from, element);

        AnonymousExpressionTracker.notifyPopped(to);

        return actualNextInsn;
    }
}
