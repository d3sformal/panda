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

import gov.nasa.jpf.abstraction.common.Predicate;

public class CompoundRefinementHeuristic extends RefinementHeuristic {
    private RefinementHeuristic[] hs;

    public CompoundRefinementHeuristic(RefinementHeuristic[] hs) {
        super(null);

        this.hs = hs;
    }

    @Override
    protected boolean refineAtomic(Predicate interpolant, MethodInfo m, int fromPC, int toPC) {
        boolean refined = false;

        for (RefinementHeuristic h : hs) {
            refined |= h.refineAtomic(interpolant, m, fromPC, toPC);
        }

        return refined;
    }
}
