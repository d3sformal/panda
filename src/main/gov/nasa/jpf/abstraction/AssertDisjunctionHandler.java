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

public class AssertDisjunctionHandler implements ExecuteInstructionHandler {

    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
        StackFrame sf = curTh.getTopFrame();

        ElementInfo arraysEI = curTh.getElementInfo(sf.pop());

        boolean[] validAssertions = new boolean[arraysEI.arrayLength()];

        for (int i = 0; i < arraysEI.arrayLength(); ++i) {
            ElementInfo arrayEI = curTh.getElementInfo(arraysEI.getReferenceElement(i));

            try {
                checkAssertionSet(arrayEI, curTh, nextInsn);

                validAssertions[i] = true;
            } catch (RuntimeException e) {
                validAssertions[i] = false;
            }
        }

        boolean foundValid = false;

        for (int i = 0; i < validAssertions.length; ++i) {
            if (foundValid && validAssertions[i]) {
                throw new RuntimeException("Line " + nextInsn.getLineNumber() + ": More than one set of assertions satisfied.");
            }

            foundValid |= validAssertions[i];
        }

        if (!foundValid) {
            throw new RuntimeException("Line " + nextInsn.getLineNumber() + ": No set of assertions satisfied.");
        }
    }

    protected void checkAssertionSet(ElementInfo arrayEI, ThreadInfo curTh, Instruction nextInsn) {
        for (int j = 0; j < arrayEI.arrayLength(); ++j) {
            ElementInfo ei = curTh.getElementInfo(arrayEI.getReferenceElement(j));

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
    }

}
