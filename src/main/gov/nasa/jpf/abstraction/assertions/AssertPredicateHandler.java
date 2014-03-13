package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.GenericProperty;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ElementInfo;

import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.PredicatesFactory;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;

public abstract class AssertPredicateHandler extends AssertHandler {

    protected void checkAssertion(ElementInfo ei, ThreadInfo curTh) {
        String assertion = new String(ei.getStringChars());

        Predicate assertedFact = Tautology.create();
        TruthValue assertedValuation = TruthValue.TRUE;

        try {
            String[] assertionParts = assertion.split(":");

            if (assertionParts.length != 2) {
                throw new Exception();
            }

            assertedFact = PredicatesFactory.createPredicateFromString(assertionParts[0]);
            assertedValuation = TruthValue.create(assertionParts[1]);
        } catch (Exception e) {
            throw new RuntimeException("Incorrect format of asserted facts: `" + assertion + "`");
        }

        checkValuation(assertedFact, assertedValuation);
    }

    protected void checkAssertionSet(ElementInfo arrayEI, ThreadInfo curTh) {
        for (int j = 0; j < arrayEI.arrayLength(); ++j) {
            ElementInfo ei = curTh.getElementInfo(arrayEI.getReferenceElement(j));

            checkAssertion(ei, curTh);
        }
    }

    protected void checkValuation(Predicate assertedFact, TruthValue assertedValuation) {
        TruthValue inferredValuation = (TruthValue) GlobalAbstraction.getInstance().processBranchingCondition(assertedFact);

        if (assertedValuation != inferredValuation) {
            throw new RuntimeException("Asserted incorrect predicate valuation: `" + assertedFact + "` expected to valuate to `" + assertedValuation + "` but actually valuated to `" + inferredValuation + "`");
        }
    }

}
