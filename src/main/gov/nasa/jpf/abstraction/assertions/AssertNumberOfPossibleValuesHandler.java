package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ElementInfo;

import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.common.PredicatesFactory;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.FlatSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseIdentifier;

import java.util.Set;
import java.util.HashSet;

public class AssertNumberOfPossibleValuesHandler extends AssertHandler {
    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
        FlatSymbolTable symbolTable = ((PredicateAbstraction) GlobalAbstraction.getInstance().get()).getSymbolTable().get(0);

        StackFrame sf = curTh.getModifiableTopFrame();

        int expectedNumber = sf.pop();
        ElementInfo ei = curTh.getElementInfo(sf.pop());

        AccessExpression expr = PredicatesFactory.createAccessExpressionFromString(new String(ei.getStringChars()));

        Set<UniverseIdentifier> values = new HashSet<UniverseIdentifier>();

        symbolTable.lookupValues(expr, values);

        if (values.size() != expectedNumber) {
            reportError(vm, nextInsn.getLineNumber(), "Unexpected number of possible values.");
        }
    }
}
