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
package gov.nasa.jpf.abstraction.heuristic;

import gov.nasa.jpf.vm.MethodInfo;

import gov.nasa.jpf.abstraction.common.BytecodeInterval;
import gov.nasa.jpf.abstraction.common.BytecodeRange;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Disjunction;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.state.SystemPredicateValuation;

public class RefinementHeuristic {
    protected SystemPredicateValuation predVal;

    public RefinementHeuristic(SystemPredicateValuation predVal) {
        this.predVal = predVal;
    }

    public boolean refine(Predicate interpolant, MethodInfo m, int fromPC, int toPC) {
        if (interpolant instanceof Conjunction) {
            Conjunction c = (Conjunction) interpolant;

            boolean a = refine(c.a, m, fromPC, toPC);
            boolean b = refine(c.b, m, fromPC, toPC);

            return a || b;
        } else if (interpolant instanceof Disjunction) {
            Disjunction d = (Disjunction) interpolant;

            boolean a = refine(d.a, m, fromPC, toPC);
            boolean b = refine(d.b, m, fromPC, toPC);

            return a || b;
        } else if (interpolant instanceof Negation) {
            Negation n = (Negation) interpolant;

            return refine(n.predicate, m, fromPC, toPC);
        } else if (interpolant instanceof Tautology) {
            return false;
        } else if (interpolant instanceof Contradiction) {
            return false;
        } else {
            return refineAtomic(interpolant, m, fromPC, toPC);
        }
    }

    protected boolean refineAtomic(Predicate interpolant, MethodInfo m, int fromPC, int toPC) {
        BytecodeRange scope = new BytecodeInterval(fromPC, toPC);

        return predVal.refine(interpolant, m, scope);
    }
}
