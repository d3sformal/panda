package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.Property;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ElementInfo;

import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.PredicatesFactory;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;

public class AssertExclusiveDisjunctionHandler extends AssertDisjunctionHandler {

    public AssertExclusiveDisjunctionHandler(Type type) {
        super(type);
    }

    @Override
    protected void respondToFindingTwoValid(VM vm, int lineNumber) {
        String reason = "Line " + lineNumber + ": More than one set of assertions satisfied.";
        Property property = new AssertProperty(reason);

        vm.getSearch().error(property);
        vm.breakTransition(reason);
    }

}
