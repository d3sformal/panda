package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.Instruction;

public class AssertValuationVisitedAtMostHandler extends AssertValuationVisitedHandler {

    @Override
    protected Class<? extends LocationAssertion> getAssertionClass() {
        return ValuationVisitedAtMostAssertion.class;
    }

    @Override
    protected  void update(VM vm, Instruction insn, PredicateValuation trackedValuation, PredicateValuation valuation, Integer limit) {
        if (!AssertStateMatchingContext.update(insn, getAssertionClass(), trackedValuation, valuation, limit)) {
            reportError(vm, insn);
        }
    }

}
