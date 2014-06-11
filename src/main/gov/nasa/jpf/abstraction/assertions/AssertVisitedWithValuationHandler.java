package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.abstraction.bytecode.AnonymousExpressionTracker;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicatesFactory;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.util.ExpressionUtil;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public abstract class AssertVisitedWithValuationHandler extends AssertHandler {

    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
        StackFrame sf = curTh.getModifiableTopFrame();

        AnonymousExpressionTracker.notifyPopped(ExpressionUtil.getExpression(sf.getOperandAttr()), 1);

        ElementInfo arrayEI = curTh.getElementInfo(sf.pop());
        int limit = sf.pop();

        PredicateValuationMap trackedValuation = new PredicateValuationMap();
        PredicateValuationMap valuation = new PredicateValuationMap();

        for (int j = 0; j < arrayEI.arrayLength(); ++j) {
            ElementInfo ei = curTh.getElementInfo(arrayEI.getReferenceElement(j));

            String str = new String(ei.getStringChars());

            try {
                String[] strParts = str.split(":");

                if (strParts.length != 2) {
                    throw new Exception();
                }

                Predicate predicate = PredicatesFactory.createPredicateFromString(strParts[0]);
                TruthValue trackedValue = TruthValue.create(strParts[1]);
                TruthValue value = PredicateAbstraction.getInstance().processBranchingCondition(predicate);

                trackedValuation.put(predicate, trackedValue);
                valuation.put(predicate, value);
            } catch (Exception e) {
                throw new RuntimeException("Incorrect format of asserted facts: `" + str + "`");
            }
        }

        update(vm, nextInsn, trackedValuation, valuation, new Integer(limit));
    }

    protected abstract void update(VM vm, Instruction insn, PredicateValuationMap trackedValuation, PredicateValuationMap valuation, Integer limit);

    protected void reportError(VM vm, Instruction nextInsn) {
        reportError(vm, nextInsn.getLineNumber(), AssertStateMatchingContext.get(nextInsn).getError());
    }

}
