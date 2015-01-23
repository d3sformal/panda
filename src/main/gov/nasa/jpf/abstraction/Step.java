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
