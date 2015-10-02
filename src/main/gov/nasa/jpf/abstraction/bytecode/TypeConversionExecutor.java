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

import gov.nasa.jpf.abstraction.common.Expression;

public class TypeConversionExecutor {

    private DataWordManipulator<?> source;
    private DataWordManipulator<?> target;

    public TypeConversionExecutor(DataWordManipulator<?> source, DataWordManipulator<?> target) {
        this.source = source;
        this.target = target;
    }

    public Instruction execute(ThreadInfo ti, TypeConvertor ins) {
        StackFrame sf = ti.getModifiableTopFrame();
        Expression expr = source.getExpression(sf);
        Instruction ret;

        ret = ins.executeConcrete(ti);

        target.setExpression(sf, expr);

        return ret;
    }

}
