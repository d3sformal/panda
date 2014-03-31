package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ElementInfo;

import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicatesFactory;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;

public abstract class AssertValuationOnEveryVisitHandler extends AssertHandler {

    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
        StackFrame sf = curTh.getModifiableTopFrame();

        ElementInfo arrayEI = curTh.getElementInfo(sf.pop());

        PredicateValuationMap valuation = new PredicateValuationMap();

        for (int j = 0; j < arrayEI.arrayLength(); ++j) {
            ElementInfo ei = curTh.getElementInfo(arrayEI.getReferenceElement(j));

            String str = new String(ei.getStringChars());

            Predicate predicate = PredicatesFactory.createPredicateFromString(str);
            TruthValue value = ((PredicateAbstraction) GlobalAbstraction.getInstance().get()).processBranchingCondition(predicate);

            valuation.put(predicate, value);
        }

        if (!AssertStateMatchingContext.update(nextInsn, getAssertionClass(), valuation)) {
            reportError(vm, nextInsn);
        }
    }

    protected abstract Class<? extends LocationAssertion> getAssertionClass();

    protected void reportError(VM vm, Instruction nextInsn) {
        reportError(vm, nextInsn.getLineNumber(), AssertStateMatchingContext.get(nextInsn).getError());
    }


}
