/*
 * Copyright (C) 2015, Charles University in Prague.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.nasa.jpf.abstraction.bytecode;

import java.util.LinkedList;
import java.util.List;

import gov.nasa.jpf.vm.ArrayFields;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.state.universe.Reference;

public class MULTIANEWARRAY extends gov.nasa.jpf.jvm.bytecode.MULTIANEWARRAY {

    public MULTIANEWARRAY (String typeName, int dimensions) {
        super(typeName, dimensions);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        return ti.createAndThrowException("java.lang.UnsupportedOperationException", "Multi-dimensional arrays not supported (use Object[])");
    }

    /*
    @Override
    public Instruction execute(ThreadInfo ti) {
        StackFrame sf = ti.getTopFrame();
        List<Expression> attrs = new LinkedList<Expression>();

        for (int i = getDimensions() - 1; i >= 0 ; --i) {
            Expression attr = (Expression) sf.getOperandAttr(i);

            if (attr == null) {
                attr = new EmptyExpression();
            }

            attrs.add(attr);
        }

        Expression attr = attrs.get(attrs.size() - 1);
        attrs.remove(attrs.size() - 1);

        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);

        Instruction actualNextInsn = super.execute(ti);

        if (JPFInstructionAdaptor.testNewArrayInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        ElementInfo array = ti.getElementInfo(sf.peek());
        AnonymousArray expression = AnonymousArray.create(new Reference(array), attr.getExpression());

        PredicateAbstraction.getInstance().processNewObject(expression, actualNextInsn.getMethodInfo(), actualNextInsn.getPosition());

        sf = ti.getModifiableTopFrame();
        sf.setOperandAttr(new Expression(null, expression));

        // ALL ELEMENTS ARE NULL
        setArrayExpressions(ti, array, attrs);

        return actualNextInsn;
    }

    private void setArrayExpressions(ThreadInfo ti, ElementInfo array, List<Expression> subList) {
        if (subList.isEmpty()) return;

        ArrayFields fields = array.getArrayFields();
        int size = array.arrayLength();

        Expression attr = subList.get(subList.size() - 1);

        for (int i = 0; i < size; ++i) {
            fields.addFieldAttr(size, i, attr);

            if (array.isReferenceArray()) {
                ElementInfo subArray = ti.getElementInfo(array.getReferenceElement(i));

                setArrayExpressions(ti, subArray, subList.subList(1, subList.size()));
            }
        }

        //subList.remove(subList.size() - 1);
    }
    */
}
