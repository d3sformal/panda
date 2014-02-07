package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.abstraction.ExecuteInstructionHandler;

import gov.nasa.jpf.Property;
import gov.nasa.jpf.GenericProperty;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.search.Search;

public abstract class AssertHandler implements ExecuteInstructionHandler {

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
            return message;
        }

        @Override
        public String getExplanation() {
            return null;
        }
    }

    protected void reportError(VM vm, int lineNumber, String message) {
        Property property = new AssertProperty("Line " + lineNumber + ": " + message);

        vm.getSearch().error(property);
        vm.breakTransition(message);
    }

}
