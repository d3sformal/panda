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
package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.state.TruthValue;

/**
 * Means of letting predicate abstraction know about valuation of the branching condition under the selected branch.
 */
public class BranchingConditionValuation implements BranchingDecision {
    private Predicate condition;
    private TruthValue valuation;

    public BranchingConditionValuation(Predicate condition, TruthValue valuation) {
        this.condition = condition;
        this.valuation = valuation;
    }

    public Predicate getCondition() {
        return condition;
    }

    public TruthValue getValuation() {
        return valuation;
    }
}
