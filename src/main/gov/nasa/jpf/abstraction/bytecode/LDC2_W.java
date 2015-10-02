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

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;

public class LDC2_W extends gov.nasa.jpf.jvm.bytecode.LDC2_W {

    public LDC2_W(long l) {
        super(l);
    }

    public LDC2_W(double d) {
        super(d);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        Instruction ret = super.execute(ti);

        StackFrame sf = ti.getModifiableTopFrame();
        Expression expression;

        switch (getType()) {
        case LONG:
            expression = Constant.create(getValue());
            break;
        case DOUBLE:
            expression = Constant.create(getDoubleValue());
            break;
        default:
            expression = null;
            break;
        }

        sf.setLongOperandAttr(expression);

        return ret;
    }

}
