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
import gov.nasa.jpf.vm.StaticElementInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadList;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.KernelState;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;

import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.SystemSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.MethodFrameSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.predicate.state.universe.StructuredValueIdentifier;
import gov.nasa.jpf.abstraction.predicate.state.universe.Universe;
import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;
import gov.nasa.jpf.abstraction.predicate.state.universe.ClassName;
import gov.nasa.jpf.abstraction.predicate.state.universe.LocalVariable;

import gov.nasa.jpf.abstraction.assertions.AssertConjunctionHandler;
import gov.nasa.jpf.abstraction.assertions.AssertDisjunctionHandler;
import gov.nasa.jpf.abstraction.assertions.AssertExclusiveDisjunctionHandler;
import gov.nasa.jpf.abstraction.assertions.AssertAliasedHandler;
import gov.nasa.jpf.abstraction.assertions.AssertNotAliasedHandler;
import gov.nasa.jpf.abstraction.assertions.AssertNumberOfPossibleValuesHandler;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

/**
 * AbstractListener monitors the state space traversal and individual instruction executions
 *
 * It informs the global abstraction about all the above mentioned events.
 */
public class AbstractListener extends PropertyListenerAdapter {
    private boolean started = false;

    private Map<String, ExecuteInstructionHandler> testMethods = new HashMap<String, ExecuteInstructionHandler>();

    private static String BaseTestClass = "gov.nasa.jpf.abstraction.predicate.BaseTest";

    public AbstractListener() {
        // Conjunction
        testMethods.put(BaseTestClass + ".assertConjunction([Ljava/lang/String;)V", new AssertConjunctionHandler());

        // Disjunction (Flat, Structured)
        testMethods.put(BaseTestClass + ".assertDisjunction([Ljava/lang/String;)V", new AssertDisjunctionHandler(AssertDisjunctionHandler.Type.ONE_PREDICATE_PER_SET));
        testMethods.put(BaseTestClass + ".assertDisjunction([[Ljava/lang/String;)V", new AssertDisjunctionHandler(AssertDisjunctionHandler.Type.MULTIPLE_PREDICATES_PER_SET));

        // Exclusive disjunction (Flat, Structured)
        testMethods.put(BaseTestClass + ".assertExclusiveDisjunction([Ljava/lang/String;)V", new AssertExclusiveDisjunctionHandler(AssertDisjunctionHandler.Type.ONE_PREDICATE_PER_SET));
        testMethods.put(BaseTestClass + ".assertExclusiveDisjunction([[Ljava/lang/String;)V", new AssertExclusiveDisjunctionHandler(AssertDisjunctionHandler.Type.MULTIPLE_PREDICATES_PER_SET));

        // Aliased
        testMethods.put(BaseTestClass + ".assertAliased([Ljava/lang/String;)V", new AssertAliasedHandler());

        // Not aliased
        testMethods.put(BaseTestClass + ".assertNotAliased([Ljava/lang/String;)V", new AssertNotAliasedHandler());

        // Number of possible values
        testMethods.put(BaseTestClass + ".assertNumberOfPossibleValues(Ljava/lang/String;I)V", new AssertNumberOfPossibleValuesHandler());
    }

	@Override
	public void vmInitialized(VM vm) {
		RunDetector.initialiseNotRunning();
		GlobalAbstraction.getInstance().start(vm.getCurrentThread());

        started = true;
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
                ExecuteInstructionHandler handler = testMethods.get(method.getFullName());

                handler.executeInstruction(vm, curTh, nextInsn);

                if (!vm.getSearch().isErrorState()) {
                    curTh.skipInstruction(curTh.getPC().getNext());
                }
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
	
    @Override
    public void threadStarted(VM vm, ThreadInfo threadInfo) {
        GlobalAbstraction.getInstance().addThread(threadInfo);
    }

    @Override
    public void threadScheduled(VM vm, ThreadInfo threadInfo) {
        GlobalAbstraction.getInstance().scheduleThread(threadInfo);
    }

    @Override
    public void choiceGeneratorRegistered(VM vm, ChoiceGenerator<?> nextCG, ThreadInfo currentThread, Instruction executedInstruction) {
        if (started) {
            collectGarbage(vm, currentThread);
        }
    }

    private void collectGarbage(VM vm, ThreadInfo ti) {
        KernelState ks = vm.getKernelState();

        ThreadList threads = ks.getThreadList();

        SystemSymbolTable symbolTable = ((PredicateAbstraction) GlobalAbstraction.getInstance().get()).getSymbolTable();
        Universe universe = symbolTable.getUniverse();

        // Add roots
        Set<UniverseIdentifier> liveRoots = new HashSet<UniverseIdentifier>();

        // All classes
        for (ClassLoaderInfo cl : ks.classLoaders) {
            if(cl.isAlive()) {
                for (StaticElementInfo sei : cl.getStatics().liveStatics()) {
                    liveRoots.add(new ClassName(sei));
                    liveRoots.add(new Reference(ti.getElementInfo(sei.getClassObjectRef())));
                }
            }
        }

        for (StructuredValueIdentifier candidate : universe.getStructuredValues()) {
            if (candidate instanceof ClassName) {
                liveRoots.add(candidate);
            }
        }

        // All thread objects, lock objects, local variables
        for (ThreadInfo thread : threads) {
            liveRoots.add(new Reference(thread.getThreadObject()));
            liveRoots.add(new Reference(thread.getLockObject()));

            for (ElementInfo lock : thread.getLockedObjects()) {
                liveRoots.add(new Reference(lock));
            }

            if (thread.isAlive()) {
                for (int depth = 0; depth < symbolTable.depth(); ++depth) {
                    MethodFrameSymbolTable currentSymbolScope = symbolTable.get(thread.getId(), depth);

                    for (Root varName : currentSymbolScope.getLocalVariables()) {
                        LocalVariable var = currentSymbolScope.getLocal(varName);

                        liveRoots.addAll(var.getPossibleValues());
                    }
                }
            }
        }

        universe.retainLiveValuesOnly(liveRoots);
    }
}
