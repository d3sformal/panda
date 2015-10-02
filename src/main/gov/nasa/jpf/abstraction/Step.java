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
package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.vm.MethodInfo;

import gov.nasa.jpf.abstraction.common.Predicate;

public class Step {
    private Predicate p;
    private MethodInfo m;
    private int pc;
    private int depth;

    public Step(Predicate p, MethodInfo m, int pc, int depth) {
        this.p = p;
        this.m = m;
        this.pc = pc;
        this.depth = depth;
    }

    public Predicate getPredicate() {
        return p;
    }

    public MethodInfo getMethod() {
        return m;
    }

    public int getPC() {
        return pc;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Step) {
            Step s = (Step) o;

            return pc == s.pc && m.equals(s.m) && p.equals(s.p);
        }

        return false;
    }
}
