package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.vm.MethodInfo;

import gov.nasa.jpf.abstraction.common.Predicate;

public class Step {
    private Predicate p;
    private MethodInfo m;
    private int pc;

    public Step(Predicate p, MethodInfo m, int pc) {
        this.p = p;
        this.m = m;
        this.pc = pc;
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
}
