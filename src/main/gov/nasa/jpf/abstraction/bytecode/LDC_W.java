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

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.state.universe.Reference;

public class LDC_W extends gov.nasa.jpf.jvm.bytecode.LDC_W {

    public LDC_W(int v) {
        super(v);
    }

    public LDC_W(float f) {
        super(f);
    }

    public LDC_W(String v, boolean isClass) {
        super(v, isClass);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        Instruction ret = super.execute(ti);

        StackFrame sf = ti.getModifiableTopFrame();
        Expression expression;

        switch (getType()) {
        case INT:
            expression = Constant.create(getValue());
            break;
        case FLOAT:
            expression = Constant.create(getFloatValue());
            break;
        case STRING:
        case CLASS:
            AnonymousObject object = AnonymousObject.create(new Reference(ti.getElementInfo(sf.peek())));

            PredicateAbstraction.getInstance().processObject(object, getMethodInfo(), getPosition());

            expression = object;
            break;
        default:
            expression = null;
            break;
        }

        sf.setOperandAttr(expression);

        return ret;
    }

}
