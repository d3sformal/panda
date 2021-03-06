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

public class AssertAliasedHandler extends AssertAliasingHandler {
    @Override
    public void checkAliasing(VM vm, int lineNumber, AccessExpression[] exprs, MethodFrameSymbolTable symbolTable) {
        // Pick a set of values as a `reference`
        boolean equal = true;

        Set<UniverseIdentifier> referenceValues = new HashSet<UniverseIdentifier>();

        if (exprs.length > 0) {
            symbolTable.lookupValues(exprs[0], referenceValues);
        }

        // See if all the rest of the sets comply with the reference one
        for (int i = 1; i < exprs.length && equal; ++i) {
            Set<UniverseIdentifier> values = new HashSet<UniverseIdentifier>();

            symbolTable.lookupValues(exprs[i], values);

            // Check whether the sets are equal
            equal &= referenceValues.size() == values.size();

            for (UniverseIdentifier id : referenceValues) {
                equal &= values.contains(id);
            }
        }

        if (!equal) {
            reportError(vm, lineNumber, "Some of the access expressions are not aliased.");
        }
    }
}
