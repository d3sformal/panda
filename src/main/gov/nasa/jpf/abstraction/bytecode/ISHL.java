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

import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Multiply;
import gov.nasa.jpf.abstraction.common.UninterpretedShiftLeft;

/**
 * Shift left integer
 * ..., value1, value2 => ..., result
 */
public class ISHL extends gov.nasa.jpf.jvm.bytecode.ISHL implements AbstractBinaryOperator<Integer> {

    IntegerBinaryOperatorExecutor executor = IntegerBinaryOperatorExecutor.getInstance();

    @Override
    public Instruction execute(ThreadInfo ti) {

        /**
         * Delegates the call to a shared object that does all the heavy lifting
         */
        return executor.execute(this, ti);
    }

    @Override
    public Attribute getResult(Integer v1, Attribute attr1, Integer v2, Attribute attr2) {
        AbstractValue abs_v1 = Attribute.getAbstractValue(attr1);
        AbstractValue abs_v2 = Attribute.getAbstractValue(attr2);

        Expression a = Attribute.getExpression(attr1);
        Expression b = Attribute.getExpression(attr2);

        Expression e;

        if (b instanceof Constant) {
            e = a;

            for (int i = ((Constant) b).value.intValue(); i > 0; --i) {
                e = Multiply.create(e, Constant.create(2));
            }
        } else {
            e = UninterpretedShiftLeft.create(a, b);
        }

        /**
         * Performs the adequate operation over abstractions
         */
        return new Attribute(Abstraction._shl(v1, abs_v1, v2, abs_v2), e);
    }

    @Override
    public Instruction executeConcrete(ThreadInfo ti) {

        /**
         * Ensures execution of the original instruction
         */
        return super.execute(ti);
    }

    @Override
    public Instruction getSelf() {

        /**
         * Ensures translation into an ordinary instruction
         */
        return this;
    }

}
