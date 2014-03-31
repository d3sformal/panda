package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.Instruction;

public class AssertRevisitedAtLeastWithValuationHandler extends AssertVisitedWithValuationHandler {

    @Override
    protected Class<? extends LocationAssertion> getAssertionClass() {
        return RevisitedAtLeastWithValuationAssertion.class;
    }

    @Override
    protected  void update(VM vm, Instruction insn, PredicateValuation trackedValuation, PredicateValuation valuation, Integer limit) {
        AssertStateMatchingContext.update(insn, getAssertionClass(), trackedValuation, valuation, limit);
    }

    @Override
    public void finish() {
        for (Instruction insn : AssertStateMatchingContext.getLocations()) {
            LocationAssertion locationAssertion = AssertStateMatchingContext.get(insn);

            if (locationAssertion instanceof RevisitedAtLeastWithValuationAssertion && locationAssertion.isViolated()) {
                reportError(VM.getVM(), insn.getLineNumber(), AssertStateMatchingContext.get(insn).getError());
            }
        }
    }
}