package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.Instruction;

public class AssertVisitedAtMostWithValuationHandler extends AssertVisitedWithValuationHandler {

    @Override
    protected  void update(VM vm, Instruction insn, PredicateValuationMap trackedValuation, PredicateValuationMap valuation, Integer limit) {
        if (AssertStateMatchingContext.getAssertion(insn, VisitedAtMostWithValuationAssertion.class).update(trackedValuation, valuation, limit).isViolated()) {
            reportError(vm, insn);
        }
    }

}
