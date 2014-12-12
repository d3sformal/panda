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
