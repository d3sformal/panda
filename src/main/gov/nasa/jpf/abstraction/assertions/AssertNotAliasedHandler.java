package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.predicate.state.MethodFrameSymbolTable;

import java.util.Set;
import java.util.HashSet;

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
