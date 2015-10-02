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
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import gov.nasa.jpf.vm.MethodInfo;

import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.util.Pair;

public class TraceFormula implements Iterable<Step> {
    public static final long serialVersionUID = 1L;

    private Stack<Step> steps = new Stack<Step>();
    private int depth = 0;

    private class MethodCall {
        MethodInfo m;
        int mCallInvoked;
        int mCallStarted;

        MethodCall(MethodInfo m, int mCallInvoked, int mCallStarted) {
            this.m = m;
            this.mCallInvoked = mCallInvoked;
            this.mCallStarted = mCallStarted;
        }

        MethodCall(MethodBoundaries mBoundaries) {
            this(mBoundaries.m, mBoundaries.mCallInvoked, mBoundaries.mCallStarted);
        }
    }

    // position of the call on current trace
    private Stack<MethodCall> unmatchedCalls = new Stack<MethodCall>();

    public class MethodBoundaries {
        MethodInfo m;
        int mCallInvoked;
        int mCallStarted;
        int mReturn;
        int mReturned;

        MethodBoundaries(MethodInfo m, int mCallInvoked, int mCallStarted, int mReturn, int mReturned) {
            this.m = m;
            this.mCallInvoked = mCallInvoked;
            this.mCallStarted = mCallStarted;
            this.mReturn = mReturn;
            this.mReturned = mReturned;
        }

        MethodBoundaries(MethodCall mCall, int mReturn, int mReturned) {
            this(mCall.m, mCall.mCallInvoked, mCall.mCallStarted, mReturn, mReturned);
        }

        public boolean equals(Object o) {
            if (this == o) return true;

            if (o instanceof MethodBoundaries) {
                MethodBoundaries b = (MethodBoundaries) o;

                return mCallInvoked == b.mCallInvoked && mCallStarted == b.mCallStarted && mReturn == b.mReturn && mReturned == b.mReturned;
            }

            return false;
        }
    }

    // position of calls and returns of methods on current trace
    private Stack<MethodBoundaries> methodBoundaries = new Stack<MethodBoundaries>();

    public Predicate toConjunction() {
        Predicate c = Tautology.create();

        for (Step s : this) {
            c = Conjunction.create(c, s.getPredicate());
        }

        return c;
    }

    public void cutAfterAssertion(MethodInfo m, int pc) {
        for (int i = size() - 1; i >= 0; --i) {
            Step s = get(i);

            if (!s.getMethod().equals(m) || s.getPC() > pc) {
                pop();
            } else {
                break;
            }
        }
    }

    private void push(Step s) {
        steps.push(s);
    }

    private void pop() {
        steps.pop();

        while (!methodBoundaries.isEmpty() && methodBoundaries.peek().mReturned > size()) {
            unmatchedCalls.push(new MethodCall(methodBoundaries.peek()));
            methodBoundaries.pop();
        }

        while (!unmatchedCalls.isEmpty() && unmatchedCalls.peek().mCallStarted > size()) {
            unmatchedCalls.pop();
        }
    }

    public Step get(int i) {
        return steps.get(i);
    }

    public int size() {
        return steps.size();
    }

    public void append(Predicate p, MethodInfo m, int pc) {
        if (size() > 0 && methodBoundaries.isEmpty() && unmatchedCalls.isEmpty()) { // Merge steps in the prefix of the trace (system startup)
            Step s = steps.pop();

            push(new Step(Conjunction.create(s.getPredicate(), p), m, pc, depth));
        } else {
            push(new Step(p, m, pc, depth));
        }
    }

    @Override
    public Iterator<Step> iterator() {
        return steps.iterator();
    }

    public void markState() {
        ++depth;
    }

    public int getDepth() {
        return depth;
    }

    public void markCallInvoked(MethodInfo m) {
        unmatchedCalls.push(new MethodCall(m, size(), size()));
    }

    public void markCallStarted() {
        unmatchedCalls.peek().mCallStarted = size();
    }

    public void markReturn() {
        methodBoundaries.push(new MethodBoundaries(unmatchedCalls.peek(), size(), size()));
        unmatchedCalls.pop();
    }

    public void markReturned() {
        methodBoundaries.peek().mReturned = size();
    }

    /**
     * @returns list of step indices constituting individual methods (excluding nested calls)
     */
    public List<Method> getMethods() {
        List<Method> methods = new ArrayList<Method>();

        while (!unmatchedCalls.isEmpty()) {
            markReturn();
            markReturned();
        }

        for (int i = methodBoundaries.size() - 1; i >= 0; --i) {
            int c = methodBoundaries.get(i).mCallStarted; // First step of the method
            int r = methodBoundaries.get(i).mReturn; // Last step of the method

            int s = c; // Step index

            Method method = new Method(methodBoundaries.get(i).m, this, methodBoundaries.get(i));

            // Until you reach end of the method
            while (s < r) {
                boolean nested = false;

                for (int j = i - 1; j >= 0; --j) {
                    int nestedC = methodBoundaries.get(j).mCallInvoked;
                    int nestedR = methodBoundaries.get(j).mReturned;

                    nested = nested || (nestedC <= s && s < nestedR);
                }

                if (!nested) {
                    method.add(s);
                }

                ++s;
            }

            if (!method.isEmpty()) {
                methods.add(method);
            }
        }

        return methods;
    }

    public boolean isPrefixOf(TraceFormula tf) {
        if (steps.size() > tf.steps.size()) {
            return false;
        }

        for (int i = 0; i < steps.size(); ++i) {
            if (!steps.get(i).equals(tf.steps.get(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public TraceFormula clone() {
        TraceFormula c = new TraceFormula();

        c.steps.addAll(steps);
        c.depth = depth;
        c.unmatchedCalls.addAll(unmatchedCalls);
        c.methodBoundaries.addAll(methodBoundaries);

        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TraceFormula) {
            TraceFormula tf = (TraceFormula) o;

            if (size() == tf.size()) {
                for (int i = 0; i < size(); ++i) {
                    Step s1 = get(i);
                    Step s2 = tf.get(i);

                    if (!s1.equals(s2)) {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }
}
