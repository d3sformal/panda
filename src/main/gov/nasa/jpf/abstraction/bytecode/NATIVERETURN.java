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
import gov.nasa.jpf.vm.NativeStackFrame;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.state.MethodFrameSymbolTable;
import gov.nasa.jpf.abstraction.state.universe.Reference;

public class NATIVERETURN extends gov.nasa.jpf.jvm.bytecode.NATIVERETURN {

    @Override
    public Instruction execute(ThreadInfo ti) {

        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);
        NativeStackFrame before = (NativeStackFrame) ti.getModifiableTopFrame();
        Object retValue = before.getReturnValue();
        Expression retExpr = ExpressionUtil.getExpression(before.getReturnAttr());

        if (retExpr == null) {
            // Push the value onto the stack
            // no matter it is native, we need to have an attribute on the stack to return it in processMethodReturn
            switch (before.getMethodInfo().getReturnTypeCode()) {
                case Types.T_ARRAY:
                case Types.T_REFERENCE:
                    AnonymousObject returnValue = AnonymousObject.create(new Reference(ti.getElementInfo(((Integer) retValue).intValue())));

                    PredicateAbstraction.getInstance().processObject(returnValue, getMethodInfo(), getPosition());

                    before.setReturnAttr(returnValue);
                    break;

                case Types.T_BOOLEAN:
                case Types.T_BYTE:
                case Types.T_CHAR:
                case Types.T_SHORT:
                case Types.T_INT:
                case Types.T_FLOAT:
                case Types.T_LONG:
                case Types.T_DOUBLE:
                    before.setReturnAttr(MethodFrameSymbolTable.DUMMY_VARIABLE);
                    break;

                case Types.T_VOID:
                default:
            }
        }

        Instruction actualNextInsn = super.execute(ti);

        StackFrame after = ti.getTopFrame();

        if (JPFInstructionAdaptor.testReturnInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        if (before.getMethodInfo().getReturnTypeCode() == Types.T_VOID) {
            PredicateAbstraction.getInstance().processVoidMethodReturn(ti, before, after);
        } else {
            PredicateAbstraction.getInstance().processMethodReturn(ti, before, after);
        }

        return actualNextInsn;
    }
}
