package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.abstraction.bytecode.AnonymousExpressionTracker;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class AssertConjunctionHandler extends AssertPredicateHandler {

    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
        StackFrame sf = curTh.getModifiableTopFrame();

        AnonymousExpressionTracker.notifyPopped(ExpressionUtil.getExpression(sf.getOperandAttr()), 1);

        ElementInfo arrayEI = curTh.getElementInfo(sf.pop());

        try {
            checkAssertionSet(arrayEI, curTh);
        } catch (Exception e) {
            reportError(vm, nextInsn.getLineNumber(), e.getMessage());
        }
    }

}
