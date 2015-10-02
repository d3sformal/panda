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

public class RevisitedAtLeastWithValuationAssertion implements LocationAssertion {
    private PredicateValuationMap valuation = null;
    private int limit = 0;
    private int visits = 0;

    public RevisitedAtLeastWithValuationAssertion update(PredicateValuationMap valuationReference, PredicateValuationMap currentValuation, Integer count) {
        limit = 1 + count;

        valuation = valuationReference;

        if (valuation.equals(currentValuation)) {
            ++visits;
        }

        return this;
    }

    @Override
    public boolean isViolated() {
        return visits < limit;
    }

    @Override
    public String getError() {
        return "location revisited too few times with valuation " + valuation + " (" + (visits - 1) + ")";
    }
}
