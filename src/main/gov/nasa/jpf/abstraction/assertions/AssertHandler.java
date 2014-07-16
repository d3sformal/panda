package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.GenericProperty;
import gov.nasa.jpf.Property;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.ExecuteInstructionHandler;

public abstract class AssertHandler extends ExecuteInstructionHandler {

    protected static class AssertProperty extends GenericProperty {

        private String message;

        public AssertProperty(String message) {
            this.message = message;
        }

        @Override
        public boolean check(Search search, VM vm) {
            return false;
        }

        @Override
        public String getErrorMessage() {
            return null;
        }

        @Override
        public String getExplanation() {
            return message;
        }
    }

    protected void reportError(VM vm, int lineNumber, String message) {
        Property property = new AssertProperty("Line " + lineNumber + ": " + message);

        vm.getSearch().error(property, vm.getClonedPath(), vm.getThreadList());
        vm.breakTransition(message);
    }

}
