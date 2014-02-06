//
// Copyright (C) 2012 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
package gov.nasa.jpf.abstraction;

// does not work well for static methods:summary not printed for errors
import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;

import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.PredicatesFactory;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;

import java.util.Map;
import java.util.HashMap;

/**
 * AbstractListener monitors the state space traversal and individual instruction executions
 *
 * It informs the global abstraction about all the above mentioned events.
 */
public class AbstractListener extends PropertyListenerAdapter {

    private interface Handler {
        public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn);
    }

    private Map<String, Handler> testMethods = new HashMap<String, Handler>();

    public AbstractListener() {
        testMethods.put("gov.nasa.jpf.abstraction.predicate.BaseTest.assertConjunction([Ljava/lang/String;)V", new Handler() {

            @Override
            public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
                StackFrame sf = curTh.getTopFrame();

                ElementInfo arrayEI = curTh.getElementInfo(sf.pop());

                for (int i = 0; i < arrayEI.arrayLength(); ++i) {
                    ElementInfo ei = curTh.getElementInfo(arrayEI.getReferenceElement(i));

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

        });
    }

	@Override
	public void vmInitialized(VM vm) {
		RunDetector.initialiseNotRunning();
		GlobalAbstraction.getInstance().start(vm.getCurrentThread());
	}
	
	@Override
	public void stateAdvanced(Search search) {
		RunDetector.advance();
		GlobalAbstraction.getInstance().forward(search.getVM().getCurrentThread().getTopFrameMethodInfo());
	}

	@Override
	public void stateBacktracked(Search search) {
		GlobalAbstraction.getInstance().backtrack(search.getVM().getCurrentThread().getTopFrameMethodInfo());
		RunDetector.backtrack();
	}

    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
        if (nextInsn instanceof InvokeInstruction) {
            InvokeInstruction invk = (InvokeInstruction) nextInsn;
            MethodInfo method = invk.getInvokedMethod();

            if (method != null && testMethods.containsKey(method.getFullName())) {
                // Do not perform this action, instead call the handler
                // This is needed to avoid an artificial INVOKE / RETURN to appear in the execution
                // INVOKE and RETURN may break things
                Handler h = testMethods.get(method.getFullName());

                h.executeInstruction(vm, curTh, nextInsn);

                System.err.println("Trying to skip the next instruction (THE INVOKE)");
                System.err.println("Original Next Instruction: " + curTh.getPC() + " " + curTh.getPC().getMethodInfo().getFullName() + " " + curTh.getPC().getPosition());

                curTh.skipInstruction(curTh.getPC().getNext());

                System.err.println("New Next Instruction: " + curTh.getPC() + " " + curTh.getPC().getMethodInfo().getFullName() + " " + curTh.getPC().getPosition());
            }
        }
    }

	@Override
	public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {
		RunDetector.detectRunning(vm, nextInsn, execInsn);
	}

    @Override
    public void classLoaded(VM vm, ClassInfo classInfo) {
        GlobalAbstraction.getInstance().processNewClass(ThreadInfo.getCurrentThread(), classInfo);
    }
	
}
