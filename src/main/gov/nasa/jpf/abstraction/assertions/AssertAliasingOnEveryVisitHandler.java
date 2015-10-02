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

import java.util.HashSet;
import java.util.Set;

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
import gov.nasa.jpf.abstraction.state.universe.UniverseIdentifier;

public abstract class AssertAliasingOnEveryVisitHandler extends AssertHandler {

    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
        StackFrame sf = curTh.getModifiableTopFrame();

        AnonymousExpressionTracker.notifyPopped(ExpressionUtil.getExpression(sf.getOperandAttr()), 1);

        ElementInfo arrayEI = curTh.getElementInfo(sf.pop());

        AliasingMap aliasing = new AliasingMap();

        for (int j = 0; j < arrayEI.arrayLength(); ++j) {
            ElementInfo ei = curTh.getElementInfo(arrayEI.getReferenceElement(j));

            String str = new String(ei.getStringChars());

            AccessExpression expression = PredicatesFactory.createAccessExpressionFromString(str);
            Set<UniverseIdentifier> values = new HashSet<UniverseIdentifier>();

            PredicateAbstraction.getInstance().getSymbolTable().get(0).lookupValues(expression, values);

            aliasing.put(expression, values);
        }

        assertAliasing(aliasing, vm, nextInsn);
    }

    protected abstract void assertAliasing(AliasingMap aliasing, VM vm, Instruction nextInsn);

    protected void reportError(VM vm, Instruction nextInsn) {
        reportError(vm, nextInsn.getLineNumber(), AssertStateMatchingContext.get(nextInsn).getError());
    }


}
