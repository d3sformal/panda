package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.vm.MethodInfo;

import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.state.SystemPredicateValuation;

public class AddEqualityRefinementHeuristic extends RefinementHeuristic {
    public AddEqualityRefinementHeuristic(SystemPredicateValuation predVal) {
        super(predVal);
    }

    @Override
    protected boolean refineAtomic(Predicate interpolant, MethodInfo m, int fromPC, int toPC) {
        boolean refined = false;

        if (interpolant instanceof LessThan) {
            LessThan lt = (LessThan) interpolant;
            Predicate eq = Equals.create(lt.a, lt.b);

            refined |= super.refineAtomic(eq, m, fromPC, toPC);
        }

        refined |= super.refineAtomic(interpolant, m, fromPC, toPC);

        return refined;
    }
}
