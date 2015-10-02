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

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.VM;

public class AssertRevisitedAtLeastWithValuationHandler extends AssertVisitedWithValuationHandler {

    @Override
    protected  void update(VM vm, Instruction insn, PredicateValuationMap trackedValuation, PredicateValuationMap valuation, Integer limit) {
        AssertStateMatchingContext.getAssertion(insn, RevisitedAtLeastWithValuationAssertion.class).update(trackedValuation, valuation, limit);
    }

    @Override
    public void finish() {
        for (Instruction insn : AssertStateMatchingContext.getLocations()) {
            LocationAssertion locationAssertion = AssertStateMatchingContext.get(insn);

            if (locationAssertion instanceof RevisitedAtLeastWithValuationAssertion && locationAssertion.isViolated()) {
                reportError(VM.getVM(), insn.getLineNumber(), AssertStateMatchingContext.get(insn).getError());
            }
        }
    }
}
