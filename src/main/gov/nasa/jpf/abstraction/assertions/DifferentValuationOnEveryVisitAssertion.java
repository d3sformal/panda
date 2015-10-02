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

public class DifferentValuationOnEveryVisitAssertion implements LocationAssertion {
    private Set<PredicateValuationMap> valuations = new HashSet<PredicateValuationMap>();
    private Set<PredicateValuationMap> duplicateValuations = new HashSet<PredicateValuationMap>();

    public DifferentValuationOnEveryVisitAssertion update(PredicateValuationMap valuation) {
        if (valuations.contains(valuation)) {
            duplicateValuations.add(valuation);
        }

        valuations.add(valuation);

        return this;
    }

    @Override
    public boolean isViolated() {
        return !duplicateValuations.isEmpty();
    }

    @Override
    public String getError() {
        return "Encountered a duplicate valuation: " + duplicateValuations.iterator().next();
    }
}
