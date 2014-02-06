package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ElementInfo;

import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.PredicatesFactory;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;

public abstract class AssertHandler implements ExecuteInstructionHandler {

    protected void checkAssertion(ElementInfo ei, ThreadInfo curTh, Instruction nextInsn) {
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
            throw new RuntimeException("Line " + nextInsn.getLineNumber() + ": Incorrect format of asserted facts: `" + assertion + "`");
        }

        TruthValue inferredValuation = (TruthValue) GlobalAbstraction.getInstance().processBranchingCondition(assertedFact);

        if (assertedValuation != inferredValuation) {
            throw new RuntimeException("Line " + nextInsn.getLineNumber() + ": Asserted incorrect predicate valuation: `" + assertedFact + "` expected to valuate to `" + assertedValuation + "` but actually valuated to `" + inferredValuation + "`");
        }
    }

    protected void checkAssertionSet(ElementInfo arrayEI, ThreadInfo curTh, Instruction nextInsn) {
        for (int j = 0; j < arrayEI.arrayLength(); ++j) {
            ElementInfo ei = curTh.getElementInfo(arrayEI.getReferenceElement(j));

            checkAssertion(ei, curTh, nextInsn);
        }
    }

}
