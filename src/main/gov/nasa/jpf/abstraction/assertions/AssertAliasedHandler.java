package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.predicate.state.FlatSymbolTable;

import java.util.Set;
import java.util.HashSet;

public class AssertAliasedHandler extends AssertAliasingHandler {
    @Override
    public void checkAliasing(VM vm, int lineNumber, AccessExpression[] exprs, FlatSymbolTable symbolTable) {
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