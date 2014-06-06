package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.abstraction.bytecode.AnonymousExpressionTracker;
import gov.nasa.jpf.abstraction.common.PredicatesFactory;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.MethodFrameSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.util.ExpressionUtil;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import java.util.HashSet;
import java.util.Set;

public class AssertNumberOfPossibleValuesHandler extends AssertHandler {
    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
        MethodFrameSymbolTable symbolTable = PredicateAbstraction.getInstance().getSymbolTable().get(0);

        StackFrame sf = curTh.getModifiableTopFrame();

        int expectedNumber = sf.pop();

        AnonymousExpressionTracker.notifyPopped(ExpressionUtil.getExpression(sf.getOperandAttr()), 1);
        ElementInfo ei = curTh.getElementInfo(sf.pop());

        AccessExpression expr = PredicatesFactory.createAccessExpressionFromString(new String(ei.getStringChars()));

        Set<UniverseIdentifier> values = new HashSet<UniverseIdentifier>();

        symbolTable.lookupValues(expr, values);

        if (values.size() != expectedNumber) {
            reportError(vm, nextInsn.getLineNumber(), "Unexpected number of possible values.");
        }
    }
}
