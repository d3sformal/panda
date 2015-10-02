/*
 * Copyright (C) 2015, Charles University in Prague.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.GenericProperty;
import gov.nasa.jpf.Property;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.ExecuteInstructionHandler;
import gov.nasa.jpf.abstraction.PredicateAbstractionRefinementSearch;

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

        PredicateAbstractionRefinementSearch search = (PredicateAbstractionRefinementSearch) vm.getSearch();

        search.error(property, vm.getClonedPath(), vm.getThreadList(), true);

        vm.breakTransition(message);
    }

}
