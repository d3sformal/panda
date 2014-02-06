package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.Property;
import gov.nasa.jpf.GenericProperty;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ElementInfo;

import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.PredicatesFactory;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.predicate.state.FlatSymbolTable;

import java.util.Set;
import java.util.HashSet;

public class AssertAliasedHandler extends AssertHandler {
    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
        FlatSymbolTable symbolTable = ((PredicateAbstraction) GlobalAbstraction.getInstance().get()).getSymbolTable().get(0);
        boolean equal = true;

        StackFrame sf = curTh.getModifiableTopFrame();

        ElementInfo arrayEI = curTh.getElementInfo(sf.pop());

        AccessExpression[] exprs = new AccessExpression[arrayEI.arrayLength()];

        for (int i = 0; i < arrayEI.arrayLength(); ++i) {
            ElementInfo ei = curTh.getElementInfo(arrayEI.getReferenceElement(i));

            exprs[i] = PredicatesFactory.createAccessExpressionFromString(new String(ei.getStringChars()));
        }

        // Pick a set of values as a `reference`
        Set<UniverseIdentifier> referenceValues = new HashSet<UniverseIdentifier>();

        if (exprs.length > 0) {
            symbolTable.lookupValues(exprs[0], referenceValues);
        }

        // See if all the rest of the sets comply with the reference one
        for (int i = 1; i < arrayEI.arrayLength() && equal; ++i) {
            Set<UniverseIdentifier> values = new HashSet<UniverseIdentifier>();

            symbolTable.lookupValues(exprs[i], values);

            // Check whether the sets are equal
            equal &= referenceValues.size() == values.size();

            for (UniverseIdentifier id : referenceValues) {
                equal &= values.contains(id);
            }
        }

        if (!equal) {
            String reason = "Line " + nextInsn.getLineNumber() + ": Some of the access expressions are not aliased.";
            Property property = new AssertProperty(reason);

            vm.getSearch().error(property);
            vm.breakTransition(reason);
        }
    }
}
