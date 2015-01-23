package gov.nasa.jpf.abstraction.heuristic;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;

import gov.nasa.jpf.abstraction.common.BytecodeUnlimitedRange;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.state.SystemPredicateValuation;

public class MethodGlobalRefinementHeuristic extends RefinementHeuristic {
    public MethodGlobalRefinementHeuristic(SystemPredicateValuation predVal) {
        super(predVal);
    }

    @Override
    protected boolean refineAtomic(Predicate interpolant, MethodInfo m, int fromPC, int toPC) {
        return predVal.refine(interpolant, m, BytecodeUnlimitedRange.getInstance());
    }
}
