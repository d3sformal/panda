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

import gov.nasa.jpf.GenericProperty;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.BranchingConditionValuation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicatesFactory;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.state.TruthValue;

public abstract class AssertPredicateHandler extends AssertHandler {

    protected void checkAssertion(ElementInfo ei, ThreadInfo curTh) {
        String assertion = new String(ei.getStringChars());

        Predicate assertedFact = Tautology.create();
        TruthValue assertedValuation = TruthValue.TRUE;

        try {
            String[] assertionParts = assertion.split(":");

            if (assertionParts.length != 2) {
                throw new Exception();
            }

            assertedFact = PredicatesFactory.createPredicateFromString(assertionParts[0]);
            assertedValuation = TruthValue.create(assertionParts[1]);
        } catch (Exception e) {
            throw new RuntimeException("Incorrect format of asserted facts: `" + assertion + "`");
        }

        checkValuation(curTh, assertedFact, assertedValuation);
    }

    protected void checkAssertionSet(ElementInfo arrayEI, ThreadInfo curTh) {
        for (int j = 0; j < arrayEI.arrayLength(); ++j) {
            ElementInfo ei = curTh.getElementInfo(arrayEI.getReferenceElement(j));

            checkAssertion(ei, curTh);
        }
    }

    protected void checkValuation(ThreadInfo curTh, Predicate assertedFact, TruthValue assertedValuation) {
        // we model assert in the same way as Java using branches: if (asserted != inferred) throw exception
        TruthValue inferredValuation = PredicateAbstraction.getInstance().processBranchingCondition(curTh.getPC().getPosition(), assertedFact);

        if (assertedValuation != inferredValuation) {
            PredicateAbstraction.getInstance().informAboutBranchingDecision(new BranchingConditionValuation(assertedFact, inferredValuation), curTh.getPC().getMethodInfo(), curTh.getPC().getPosition());

            throw new RuntimeException("Asserted incorrect predicate valuation: `" + assertedFact + "` expected to valuate to `" + assertedValuation + "` but actually valuated to `" + inferredValuation + "`");
        }
    }

}
