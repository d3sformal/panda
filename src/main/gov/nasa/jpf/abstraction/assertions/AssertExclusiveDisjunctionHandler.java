package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.vm.VM;

public class AssertExclusiveDisjunctionHandler extends AssertDisjunctionHandler {

    public AssertExclusiveDisjunctionHandler(Type type) {
        super(type);
    }

    @Override
    protected void respondToFindingTwoValid(VM vm, int lineNumber) {
        reportError(vm, lineNumber, "More than one set of assertions satisfied.");
    }

}
