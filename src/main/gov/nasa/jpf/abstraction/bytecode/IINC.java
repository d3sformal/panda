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
import gov.nasa.jpf.abstraction.FocusAbstractChoiceGenerator;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.common.Add;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;

/**
 * Increment local variable by constant
 * No change
 */
public class IINC extends gov.nasa.jpf.jvm.bytecode.IINC {

    public IINC(int localVarIndex, int increment) {
        super(localVarIndex, increment);
    }

    public Instruction execute(ThreadInfo ti) {

        SystemState ss = ti.getVM().getSystemState();
        StackFrame sf = ti.getModifiableTopFrame();
        AbstractValue abs_v = Attribute.getAbstractValue(sf.getLocalAttr(index));

        AccessExpression path = null;
        Expression expression = null;

        path = DefaultRoot.create(sf.getLocalVarInfo(getIndex()) == null ? null : sf.getLocalVarInfo(getIndex()).getName(), getIndex());
        expression = Add.create(path, Constant.create(increment));

        if (abs_v == null) {
            Attribute result = new Attribute(null, expression);

            sf.setLocalAttr(index, result);
            sf.setLocalVariable(index, sf.getLocalVariable(index) + increment, false);
        } else {
            Attribute result = new Attribute(Abstraction._add(0, abs_v, increment, null), expression);

            if (Attribute.getAbstractValue(result).isComposite()) {
                if (!ti.isFirstStepInsn()) { // first time around
                    int size = Attribute.getAbstractValue(result).getTokensNumber();

                    ChoiceGenerator<?> cg = new FocusAbstractChoiceGenerator(size);
                    ss.setNextChoiceGenerator(cg);

                    return this;
                } else { // this is what really returns results
                    ChoiceGenerator<?> cg = ss.getChoiceGenerator();

                    assert (cg instanceof FocusAbstractChoiceGenerator);

                    int key = (Integer) cg.getNextChoice();
                    Attribute.setAbstractValue(result, Attribute.getAbstractValue(result).getToken(key));
                }
            }

            sf.setLocalAttr(index, result);
            sf.setLocalVariable(index, 0, false);
        }

        GlobalAbstraction.getInstance().processPrimitiveStore(expression, path);

        return getNext(ti);
    }

}
