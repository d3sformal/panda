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

public class AssertDisjunctionHandler extends AssertHandler {

    public enum Type {
        ONE_PREDICATE_PER_SET,
        MULTIPLE_PREDICATES_PER_SET
    }

    private Type type;

    public AssertDisjunctionHandler(Type type) {
        this.type = type;
    }

    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
        StackFrame sf = curTh.getTopFrame();

        ElementInfo arrayEI = curTh.getElementInfo(sf.pop());

        boolean foundValid = false;
        boolean foundTwoValid = false;

        for (int i = 0; i < arrayEI.arrayLength(); ++i) {
            ElementInfo ei = curTh.getElementInfo(arrayEI.getReferenceElement(i));

            try {
                switch (type) {
                    case ONE_PREDICATE_PER_SET:
                        checkAssertion(ei, curTh, nextInsn);
                        break;

                    case MULTIPLE_PREDICATES_PER_SET:
                        checkAssertionSet(ei, curTh, nextInsn);
                        break;
                }

                foundTwoValid |= foundValid;
                foundValid |= true;
            } catch (RuntimeException e) {
            }
        }

        if (foundTwoValid) {
            respondToFindingTwoValid(nextInsn.getLineNumber());
        }

        if (!foundValid) {
            respondToNotFindingAnyValid(nextInsn.getLineNumber());
        }
    }

    protected void respondToFindingTwoValid(int lineNumber) {
    }

    protected void respondToNotFindingAnyValid(int lineNumber) {
        throw new RuntimeException("Line " + lineNumber + ": No set of assertions satisfied.");
    }

}
