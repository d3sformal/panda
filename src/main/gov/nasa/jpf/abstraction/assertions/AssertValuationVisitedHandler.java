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

public abstract class AssertValuationVisitedHandler extends AssertHandler {

    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
        StackFrame sf = curTh.getModifiableTopFrame();

        ElementInfo arrayEI = curTh.getElementInfo(sf.pop());
        int limit = sf.pop();

        PredicateValuation trackedValuation = new PredicateValuation();
        PredicateValuation valuation = new PredicateValuation();

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
                TruthValue value = ((PredicateAbstraction) GlobalAbstraction.getInstance().get()).processBranchingCondition(predicate);

                trackedValuation.put(predicate, trackedValue);
                valuation.put(predicate, value);
            } catch (Exception e) {
                throw new RuntimeException("Incorrect format of asserted facts: `" + str + "`");
            }
        }

        update(vm, nextInsn, trackedValuation, valuation, new Integer(limit));
    }

    protected abstract Class<? extends LocationAssertion> getAssertionClass();

    protected abstract void update(VM vm, Instruction insn, PredicateValuation trackedValuation, PredicateValuation valuation, Integer limit);

    protected void reportError(VM vm, Instruction nextInsn) {
        reportError(vm, nextInsn.getLineNumber(), AssertStateMatchingContext.get(nextInsn).getError());
    }

}
