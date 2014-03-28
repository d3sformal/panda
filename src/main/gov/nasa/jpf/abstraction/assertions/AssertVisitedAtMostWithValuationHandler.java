package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.Instruction;

public class AssertVisitedAtMostWithValuationHandler extends AssertVisitedWithValuationHandler {

    @Override
    protected Class<? extends LocationAssertion> getAssertionClass() {
        return VisitedAtMostWithValuationAssertion.class;
    }

    @Override
    protected  void update(VM vm, Instruction insn, PredicateValuation trackedValuation, PredicateValuation valuation, Integer limit) {
        if (!AssertStateMatchingContext.update(insn, getAssertionClass(), trackedValuation, valuation, limit)) {
            reportError(vm, insn);
        }
    }

}
