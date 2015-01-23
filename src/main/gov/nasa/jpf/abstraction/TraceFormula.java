package gov.nasa.jpf.abstraction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import gov.nasa.jpf.vm.MethodInfo;

import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Tautology;

public class TraceFormula implements Iterable<Step> {
    public static final long serialVersionUID = 1L;

    private Stack<Step> steps = new Stack<Step>();
    private int depth = 0;

    private class MethodCall {
        int mCallInvoked;
        int mCallStarted;

        MethodCall(int mCallInvoked, int mCallStarted) {
            this.mCallInvoked = mCallInvoked;
            this.mCallStarted = mCallStarted;
        }

        MethodCall(MethodBoundaries mBoundaries) {
            this(mBoundaries.mCallInvoked, mBoundaries.mCallStarted);
        }
    }

    // position of the call on current trace
    private Stack<MethodCall> unmatchedCalls = new Stack<MethodCall>();

    private class MethodBoundaries {
        int mCallInvoked;
        int mCallStarted;
        int mReturn;
        int mReturned;

        MethodBoundaries(int mCallInvoked, int mCallStarted, int mReturn, int mReturned) {
            this.mCallInvoked = mCallInvoked;
            this.mCallStarted = mCallStarted;
            this.mReturn = mReturn;
            this.mReturned = mReturned;
        }

        MethodBoundaries(MethodCall mCall, int mReturn, int mReturned) {
            this(mCall.mCallInvoked, mCall.mCallStarted, mReturn, mReturned);
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
        push(new Step(p, m, pc, depth));
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

    public void markCallInvoked() {
        unmatchedCalls.push(new MethodCall(size(), size()));
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
    public List<List<Integer>> getMethods() {
        List<List<Integer>> methods = new ArrayList<List<Integer>>();

        while (!unmatchedCalls.isEmpty()) {
            markReturn();
        }

        for (int i = methodBoundaries.size() - 1; i >= 0; --i) {
            int c = methodBoundaries.get(i).mCallStarted; // First step of the method
            int r = methodBoundaries.get(i).mReturn; // Last step of the method

            int s = c; // Step index

            List<Integer> method = new ArrayList<Integer>();

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
