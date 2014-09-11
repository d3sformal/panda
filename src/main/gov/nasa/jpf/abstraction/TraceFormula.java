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
    private Stack<Integer> unmatchedCalls = new Stack<Integer>();
    private Stack<Pair<Integer, Integer>> methodBoundaries = new Stack<Pair<Integer, Integer>>();

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

        while (!methodBoundaries.isEmpty() && methodBoundaries.peek().getSecond() > size()) {
            unmatchedCalls.push(methodBoundaries.peek().getFirst());
            methodBoundaries.pop();
        }

        while (!unmatchedCalls.isEmpty() && unmatchedCalls.peek() > size()) {
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
        push(new Step(p, m, pc));
    }

    @Override
    public Iterator<Step> iterator() {
        return steps.iterator();
    }

    public void markCall() {
        unmatchedCalls.push(size());
    }

    public void markReturn() {
        methodBoundaries.push(new Pair<Integer, Integer>(unmatchedCalls.peek(), size()));
        unmatchedCalls.pop();
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
            int c = methodBoundaries.get(i).getFirst(); // First step of the method
            int r = methodBoundaries.get(i).getSecond(); // Last step of the method

            int s = c; // Step index

            List<Integer> method = new ArrayList<Integer>();

            // Until you reach end of the method
            while (s < r) {
                boolean nested = false;

                for (int j = i - 1; j >= 0; --j) {
                    int nestedC = methodBoundaries.get(j).getFirst();
                    int nestedR = methodBoundaries.get(j).getSecond();

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

    @Override
    public TraceFormula clone() {
        TraceFormula c = new TraceFormula();

        c.steps.addAll(steps);
        c.unmatchedCalls.addAll(unmatchedCalls);
        c.methodBoundaries.addAll(methodBoundaries);

        return c;
    }
}
