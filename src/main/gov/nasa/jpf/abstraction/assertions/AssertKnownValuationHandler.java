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

import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.state.TruthValue;

public class AssertKnownValuationHandler extends AssertConjunctionHandler {

    @Override
    protected void checkValuation(ThreadInfo curTh, Predicate assertedFact, TruthValue assertedValuation) {
        TruthValue knownValuation = PredicateAbstraction.getInstance().getPredicateValuation().get(assertedFact);

        if (assertedValuation != knownValuation) {
            throw new RuntimeException("Asserted incorrect predicate valuation: `" + assertedFact + "` expected to valuate to `" + assertedValuation + "` but actually valuated to `" + knownValuation + "`");
        }
    }

}
