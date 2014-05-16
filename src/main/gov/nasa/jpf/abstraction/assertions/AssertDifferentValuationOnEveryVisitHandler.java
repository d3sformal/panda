package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.Instruction;

public class AssertDifferentValuationOnEveryVisitHandler extends AssertValuationOnEveryVisitHandler {

    @Override
    public void assertValuation(PredicateValuationMap valuation, VM vm, Instruction nextInsn) {
        if (AssertStateMatchingContext.getAssertion(nextInsn, DifferentValuationOnEveryVisitAssertion.class).update(valuation).isViolated()) {
            reportError(vm, nextInsn);
        }
    }

}
