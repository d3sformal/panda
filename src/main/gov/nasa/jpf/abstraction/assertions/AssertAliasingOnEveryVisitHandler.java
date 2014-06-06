package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.abstraction.bytecode.AnonymousExpressionTracker;
import gov.nasa.jpf.abstraction.common.PredicatesFactory;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.util.ExpressionUtil;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import java.util.HashSet;
import java.util.Set;

public abstract class AssertAliasingOnEveryVisitHandler extends AssertHandler {

    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
        StackFrame sf = curTh.getModifiableTopFrame();

        AnonymousExpressionTracker.notifyPopped(ExpressionUtil.getExpression(sf.getOperandAttr()), 1);

        ElementInfo arrayEI = curTh.getElementInfo(sf.pop());

        AliasingMap aliasing = new AliasingMap();

        for (int j = 0; j < arrayEI.arrayLength(); ++j) {
            ElementInfo ei = curTh.getElementInfo(arrayEI.getReferenceElement(j));

            String str = new String(ei.getStringChars());

            AccessExpression expression = PredicatesFactory.createAccessExpressionFromString(str);
            Set<UniverseIdentifier> values = new HashSet<UniverseIdentifier>();

            PredicateAbstraction.getInstance().getSymbolTable().get(0).lookupValues(expression, values);

            aliasing.put(expression, values);
        }

        assertAliasing(aliasing, vm, nextInsn);
    }

    protected abstract void assertAliasing(AliasingMap aliasing, VM vm, Instruction nextInsn);

    protected void reportError(VM vm, Instruction nextInsn) {
        reportError(vm, nextInsn.getLineNumber(), AssertStateMatchingContext.get(nextInsn).getError());
    }


}
