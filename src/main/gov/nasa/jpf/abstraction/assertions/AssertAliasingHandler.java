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
package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.bytecode.AnonymousExpressionTracker;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.abstraction.common.PredicatesFactory;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.state.MethodFrameSymbolTable;

public abstract class AssertAliasingHandler extends AssertHandler {
    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
        MethodFrameSymbolTable symbolTable = PredicateAbstraction.getInstance().getSymbolTable().get(0);

        StackFrame sf = curTh.getModifiableTopFrame();

        AnonymousExpressionTracker.notifyPopped(ExpressionUtil.getExpression(sf.getOperandAttr()), 1);

        ElementInfo arrayEI = curTh.getElementInfo(sf.pop());

        AccessExpression[] exprs = new AccessExpression[arrayEI.arrayLength()];

        for (int i = 0; i < arrayEI.arrayLength(); ++i) {
            ElementInfo ei = curTh.getElementInfo(arrayEI.getReferenceElement(i));

            exprs[i] = PredicatesFactory.createAccessExpressionFromString(new String(ei.getStringChars()));
        }

        checkAliasing(vm, nextInsn.getLineNumber(), exprs, symbolTable);
    }

    protected abstract void checkAliasing(VM vm, int lineNumber, AccessExpression[] exprs, MethodFrameSymbolTable symbolTable);
}
