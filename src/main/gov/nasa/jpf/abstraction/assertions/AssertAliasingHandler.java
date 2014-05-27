package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ElementInfo;

import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.bytecode.AnonymousExpressionTracker;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.PredicatesFactory;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.MethodFrameSymbolTable;

public abstract class AssertAliasingHandler extends AssertHandler {
    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
        MethodFrameSymbolTable symbolTable = ((PredicateAbstraction) GlobalAbstraction.getInstance().get()).getSymbolTable().get(0);

        StackFrame sf = curTh.getModifiableTopFrame();

        AnonymousExpressionTracker.notifyPopped(Attribute.getExpression(sf.getOperandAttr()), 1);

        ElementInfo arrayEI = curTh.getElementInfo(sf.pop());

        AccessExpression[] exprs = new AccessExpression[arrayEI.arrayLength()];

        for (int i = 0; i < arrayEI.arrayLength(); ++i) {
            ElementInfo ei = curTh.getElementInfo(arrayEI.getReferenceElement(i));

            exprs[i] = PredicatesFactory.createAccessExpressionFromString(new String(ei.getStringChars()));
        }

        checkAliasing(vm, nextInsn.getLineNumber(), exprs, symbolTable);
    }

    protected abstract void checkAliasing(VM vm, int lineNumber, AccessExpression[] exprs, MethodFrameSymbolTable symbolTable);
}
