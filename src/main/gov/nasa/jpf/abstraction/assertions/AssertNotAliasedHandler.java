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

import java.util.HashSet;
import java.util.Set;

import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.state.MethodFrameSymbolTable;
import gov.nasa.jpf.abstraction.state.universe.UniverseIdentifier;

public class AssertNotAliasedHandler extends AssertAliasingHandler {
    @Override
    public void checkAliasing(VM vm, int lineNumber, AccessExpression[] exprs, MethodFrameSymbolTable symbolTable) {
        // See if all the rest of the sets comply with the reference one
        for (int i = 0; i < exprs.length; ++i) {
            Set<UniverseIdentifier> currentValues = new HashSet<UniverseIdentifier>();

            symbolTable.lookupValues(exprs[i], currentValues);

            for (int j = i + 1; j < exprs.length; ++j) {
                Set<UniverseIdentifier> otherValues = new HashSet<UniverseIdentifier>();

                symbolTable.lookupValues(exprs[j], otherValues);

                // Check whether the sets are equal
                boolean overlap = false;

                for (UniverseIdentifier id : currentValues) {
                    overlap |= otherValues.contains(id);
                }

                for (UniverseIdentifier id : otherValues) {
                    overlap |= currentValues.contains(id);
                }

                if (overlap) {
                    reportError(vm, lineNumber, "Some of the access expressions are partially aliased.");
                }
            }
        }
    }
}
