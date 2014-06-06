package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.VM;

public class AssertSameAliasingOnEveryVisitHandler extends AssertAliasingOnEveryVisitHandler {

    @Override
    public void assertAliasing(AliasingMap aliasing, VM vm, Instruction nextInsn) {
        if (AssertStateMatchingContext.getAssertion(nextInsn, SameAliasingOnEveryVisitAssertion.class).update(aliasing).isViolated()) {
            reportError(vm, nextInsn);
        }
    }

}
