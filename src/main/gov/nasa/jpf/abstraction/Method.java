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

import java.util.ArrayList;

import gov.nasa.jpf.vm.MethodInfo;

public class Method extends ArrayList<Integer> {
    private static final long serialVersionUID = 0L;
    private MethodInfo info;
    private TraceFormula trace;
    private TraceFormula.MethodBoundaries boundaries;

    public Method(MethodInfo info, TraceFormula trace, TraceFormula.MethodBoundaries b) {
        this.info = info;
        this.trace = trace;
        this.boundaries = b;
    }

    public MethodInfo getInfo() {
        return info;
    }

    public Step getStep(int i) {
        return trace.get(get(i));
    }

    public TraceFormula getTrace() {
        return trace;
    }

    public int getInit() {
        return boundaries.mCallInvoked;
    }

    public int getCall() {
        return boundaries.mCallStarted;
    }

    public int getReturn() {
        return boundaries.mReturn;
    }

    public int getFinish() {
        return boundaries.mReturned;
    }

    public Step getInitStep() {
        return trace.get(getInit());
    }

    public boolean equals(Object o) {
        if (o instanceof Method) {
            Method m = (Method) o;

            return info.equals(m.info) && boundaries == m.boundaries && trace == m.trace && super.equals(o);
        }

        return false;
    }
}
