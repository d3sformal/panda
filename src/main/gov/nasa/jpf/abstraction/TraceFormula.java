package gov.nasa.jpf.abstraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import gov.nasa.jpf.vm.MethodInfo;

import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Disjunction;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.impl.ArraysAssign;
import gov.nasa.jpf.abstraction.common.impl.FieldAssign;
import gov.nasa.jpf.abstraction.common.impl.VariableAssign;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ArrayElementWrite;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.Unknown;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.state.MethodFrameSymbolTable;
import gov.nasa.jpf.abstraction.state.SystemSymbolTable;
import gov.nasa.jpf.abstraction.state.universe.Reference;
import gov.nasa.jpf.abstraction.state.universe.UniverseIdentifier;
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

        computeUnknownInfluence(s.getPredicate());
    }

    Map<String, Set<AccessExpression>> unknownInfluence = new HashMap<String, Set<AccessExpression>>();

    public void registerUnknown(String name) {
        unknownInfluence.put(name, new HashSet<AccessExpression>());
    }

    public void computeUnknownInfluence(Predicate p) {
        computeUnknownInfluenceRecursive(p);
        //System.out.println(unknownInfluence.get("ssa_0_frame_10_unknown_pc0"));
    }

    private void computeUnknownInfluenceRecursive(Predicate p) {
        if (p instanceof Negation) {
            Negation n = (Negation) p;

            computeUnknownInfluence(n.predicate);
        } else if (p instanceof Conjunction) {
            Conjunction c = (Conjunction) p;

            computeUnknownInfluence(c.a);
            computeUnknownInfluence(c.b);
        } else if (p instanceof Disjunction) {
            Disjunction d = (Disjunction) p;

            computeUnknownInfluence(d.a);
            computeUnknownInfluence(d.b);
        } else if (p instanceof VariableAssign) {
            Set<AccessExpression> exprs = new HashSet<AccessExpression>();

            VariableAssign va = (VariableAssign) p;

            va.expression.addAccessExpressionsToSet(exprs);

            computeUnknownInfluence(va.variable, exprs);
        } else if (p instanceof FieldAssign) {
            FieldAssign fa = (FieldAssign) p;

            if (fa.newField instanceof ObjectFieldWrite) {
                Set<AccessExpression> exprs = new HashSet<AccessExpression>();

                ObjectFieldWrite fw = (ObjectFieldWrite) fa.newField;
                ObjectFieldRead fr = DefaultObjectFieldRead.create(fw.getObject(), fa.field);

                fw.getNewValue().addAccessExpressionsToSet(exprs);

                computeUnknownInfluence(fr, exprs);
            }
        } else if (p instanceof ArraysAssign) {
            ArraysAssign aa = (ArraysAssign) p;

            if (aa.newArrays instanceof ArrayElementWrite) {
                Set<AccessExpression> exprs = new HashSet<AccessExpression>();

                ArrayElementWrite aw = (ArrayElementWrite) aa.newArrays;
                ArrayElementRead ar = DefaultArrayElementRead.create(aw.getArray(), aa.arrays, aw.getIndex());

                aw.getIndex().addAccessExpressionsToSet(exprs);
                aw.getNewValue().addAccessExpressionsToSet(exprs);

                computeUnknownInfluence(ar, exprs);
            }
        /*
        } else if (p instanceof Equals) {
            p.addAccessExpressionsToSet(exprs);

            computeUnknownInfluence(exprs, exprs);
        } else if (p instanceof LessThan) {
            p.addAccessExpressionsToSet(exprs);

            computeUnknownInfluence(exprs);
        */
        }
    }

    public Set<Unknown> getInfluentialUnknowns(AccessExpression ae) {
        Set<Unknown> ret = new HashSet<Unknown>();

        addInfluentialUnknownsToSet(ae, ret);

        return ret;
    }

    public Set<Unknown> getInfluentialUnknowns(Set<AccessExpression> exprs) {
        Set<Unknown> ret = new HashSet<Unknown>();

        for (AccessExpression ae : exprs) {
            addInfluentialUnknownsToSet(ae, ret);
        }

        return ret;
    }

    public void dropInfluencesFromFrame(int frame) {
        Set<AccessExpression> toBeRemoved = new HashSet<AccessExpression>();
        Set<AccessExpression> expr = new HashSet<AccessExpression>();

        for (String u : unknownInfluence.keySet()) {
            for (AccessExpression ae : unknownInfluence.get(u)) {
                ae.addAccessExpressionsToSet(expr);

                for (AccessExpression ae2 : expr) {
                    Root r = ae2.getRoot();

                    if (r.getName().matches("^ssa_[0-9]\\+_frame_" + frame)) {
                        toBeRemoved.add(ae);

                        break;
                    }
                }

                expr.clear();
            }

            unknownInfluence.get(u).removeAll(toBeRemoved);

            toBeRemoved.clear();
        }
    }

    protected void addInfluentialUnknownsToSet(AccessExpression ae, Set<Unknown> ret) {
        PredicateAbstraction abs = PredicateAbstraction.getInstance();
        SSAFormulaIncarnationsManager ssa = abs.getSSAManager();
        AccessExpression ssaExpr = ssa.getSymbolIncarnation(ae, 0);
        Map<String, Unknown> unknowns = abs.getUnknowns();

        for (String u : unknownInfluence.keySet()) {
            Set<AccessExpression> influenced = unknownInfluence.get(u);

            for (AccessExpression i : influenced) {
                if (i.isPrefixOf(ssaExpr)) {
                    ret.add(unknowns.get(u));
                }
            }
        }
    }

    private void computeUnknownInfluence(AccessExpression expr, Set<AccessExpression> dep) {
        for (String u : unknownInfluence.keySet()) {
            Set<AccessExpression> influenced = unknownInfluence.get(u);

            for (AccessExpression ae : dep) {
                boolean isUnknown = false;

                if (ae instanceof Root) {
                    Root r = (Root) ae;

                    if (r.getName().equals(u)) {
                        isUnknown = true;
                    }
                }

                boolean isInfluenced = false;

                for (AccessExpression i : influenced) {
                    if (i.isPrefixOf(ae) || ae.isPrefixOf(i)) {
                        isInfluenced = true;

                        break;
                    }
                }

                if (isUnknown || isInfluenced) {
                    addInfluence(influenced, expr);

                    return;
                }
            }
        }
    }

    private void addInfluence(Set<AccessExpression> influenced, AccessExpression ae) {
        PredicateAbstraction abs = PredicateAbstraction.getInstance();
        SystemSymbolTable sys = abs.getSymbolTable();
        MethodFrameSymbolTable top = sys.get(0);
        Set<UniverseIdentifier> ids = new HashSet<UniverseIdentifier>();
        Set<AccessExpression> alias = new HashSet<AccessExpression>();
        SSAFormulaIncarnationsManager ssa = abs.getSSAManager();
        AccessExpression ae1 = ssa.clear(ae);

        top.lookupValues(ae1, ids);

        System.out.println("Adding influence to: " + ae + " (" + ae1 + ") = " + ids);


        for (UniverseIdentifier id : ids) {
            //System.out.println(" \tParents: " + top.getUniverse().get(id).getParentSlots());
            for (int i = 0; i < sys.depth(); ++i) {
                MethodFrameSymbolTable sym = sys.get(i);

                sym.valueToAccessExpressions(id, ae.getLength() + 1, alias); // TODO: length = ae.length + maxParamLength of current frame's actual param to the nested frame
                System.out.println("\tAliases of length " + ae.getLength() + " at depth " + i + ": " + alias);

                for (AccessExpression ae2 : alias) {
                    influenced.add(ssa.getSymbolIncarnation(ae2, i));
                }
            }
        }

        influenced.add(ae);
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

        for (String u : unknownInfluence.keySet()) {
            Set<AccessExpression> influenced = new HashSet<AccessExpression>();

            influenced.addAll(unknownInfluence.get(u));

            c.unknownInfluence.put(u, influenced);
        }

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
