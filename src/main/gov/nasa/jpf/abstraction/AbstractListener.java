package gov.nasa.jpf.abstraction;

// does not work well for static methods:summary not printed for errors

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.assertions.AssertAliasedHandler;
import gov.nasa.jpf.abstraction.assertions.AssertConjunctionHandler;
import gov.nasa.jpf.abstraction.assertions.AssertDifferentValuationOnEveryVisitHandler;
import gov.nasa.jpf.abstraction.assertions.AssertDisjunctionHandler;
import gov.nasa.jpf.abstraction.assertions.AssertExclusiveDisjunctionHandler;
import gov.nasa.jpf.abstraction.assertions.AssertKnownValuationHandler;
import gov.nasa.jpf.abstraction.assertions.AssertMayBeAliasedHandler;
import gov.nasa.jpf.abstraction.assertions.AssertNotAliasedHandler;
import gov.nasa.jpf.abstraction.assertions.AssertNumberOfPossibleValuesHandler;
import gov.nasa.jpf.abstraction.assertions.AssertRevisitedAtLeastHandler;
import gov.nasa.jpf.abstraction.assertions.AssertRevisitedAtLeastWithValuationHandler;
import gov.nasa.jpf.abstraction.assertions.AssertSameAliasingOnEveryVisitHandler;
import gov.nasa.jpf.abstraction.assertions.AssertSameValuationOnEveryVisitHandler;
import gov.nasa.jpf.abstraction.assertions.AssertVisitedAtMostHandler;
import gov.nasa.jpf.abstraction.assertions.AssertVisitedAtMostWithValuationHandler;
import gov.nasa.jpf.abstraction.inspection.AliasingDumper;
import gov.nasa.jpf.abstraction.smt.SMT;
import gov.nasa.jpf.abstraction.util.RunDetector;

/**
 * AbstractListener monitors the state space traversal and individual instruction executions
 *
 * It informs the global abstraction about all the above mentioned events.
 */
public class AbstractListener extends PropertyListenerAdapter {
    private Map<String, ExecuteInstructionHandler> debugMethods = new HashMap<String, ExecuteInstructionHandler>();
    private Map<String, ExecuteInstructionHandler> testMethods = new HashMap<String, ExecuteInstructionHandler>();

    private static String DebugClass = "gov.nasa.jpf.abstraction.Debug";
    private static String BaseTestClass = "gov.nasa.jpf.abstraction.BaseTest";
    private static String StateMatchingTestClass = "gov.nasa.jpf.abstraction.statematch.StateMatchingTest";

    public AbstractListener() {
        // Debug
        debugMethods.put(DebugClass + ".dumpAliasing(Ljava/lang/String;)V", new AliasingDumper());

        // Conjunction
        testMethods.put(BaseTestClass + ".assertConjunction([Ljava/lang/String;)V", new AssertConjunctionHandler());

        // Disjunction (Flat, Structured)
        testMethods.put(BaseTestClass + ".assertDisjunction([Ljava/lang/String;)V", new AssertDisjunctionHandler(AssertDisjunctionHandler.Type.ONE_PREDICATE_PER_SET));
        testMethods.put(BaseTestClass + ".assertDisjunction([[Ljava/lang/String;)V", new AssertDisjunctionHandler(AssertDisjunctionHandler.Type.MULTIPLE_PREDICATES_PER_SET));

        // Exclusive disjunction (Flat, Structured)
        testMethods.put(BaseTestClass + ".assertExclusiveDisjunction([Ljava/lang/String;)V", new AssertExclusiveDisjunctionHandler(AssertDisjunctionHandler.Type.ONE_PREDICATE_PER_SET));
        testMethods.put(BaseTestClass + ".assertExclusiveDisjunction([[Ljava/lang/String;)V", new AssertExclusiveDisjunctionHandler(AssertDisjunctionHandler.Type.MULTIPLE_PREDICATES_PER_SET));

        // Exact valuation stored
        testMethods.put(BaseTestClass + ".assertKnownValuation([Ljava/lang/String;)V", new AssertKnownValuationHandler());

        // Aliased
        testMethods.put(BaseTestClass + ".assertAliased([Ljava/lang/String;)V", new AssertAliasedHandler());

        // Not aliased
        testMethods.put(BaseTestClass + ".assertNotAliased([Ljava/lang/String;)V", new AssertNotAliasedHandler());

        // May be aliased
        testMethods.put(BaseTestClass + ".assertMayBeAliased([Ljava/lang/String;)V", new AssertMayBeAliasedHandler());

        // Number of possible values
        testMethods.put(BaseTestClass + ".assertNumberOfPossibleValues(Ljava/lang/String;I)V", new AssertNumberOfPossibleValuesHandler());

        // Number of visits
        testMethods.put(StateMatchingTestClass + ".assertVisitedAtMost(I)V", new AssertVisitedAtMostHandler());
        testMethods.put(StateMatchingTestClass + ".assertRevisitedAtLeast(I)V", new AssertRevisitedAtLeastHandler());

        // Valuations on all paths reaching a shared location
        testMethods.put(StateMatchingTestClass + ".assertSameValuationOnEveryVisit([Ljava/lang/String;)V", new AssertSameValuationOnEveryVisitHandler());
        testMethods.put(StateMatchingTestClass + ".assertDifferentValuationOnEveryVisit([Ljava/lang/String;)V", new AssertDifferentValuationOnEveryVisitHandler());

        // Number of visits with a specific valuation
        testMethods.put(StateMatchingTestClass + ".assertVisitedAtMostWithValuation(I[Ljava/lang/String;)V", new AssertVisitedAtMostWithValuationHandler());
        testMethods.put(StateMatchingTestClass + ".assertRevisitedAtLeastWithValuation(I[Ljava/lang/String;)V", new AssertRevisitedAtLeastWithValuationHandler());

        // Aliasing
        testMethods.put(StateMatchingTestClass + ".assertSameAliasingOnEveryVisit([Ljava/lang/String;)V", new AssertSameAliasingOnEveryVisitHandler());
    }

    @Override
    public void vmInitialized(VM vm) {
        RunDetector.initialiseNotRunning();
        PredicateAbstraction.getInstance().start(vm.getCurrentThread());
    }

    @Override
    public void stateAdvanced(Search search) {
        RunDetector.advance();
        PredicateAbstraction.getInstance().forward(search.getVM().getCurrentThread().getTopFrameMethodInfo());
    }

    @Override
    public void stateBacktracked(Search search) {
        PredicateAbstraction.getInstance().backtrack(search.getVM().getCurrentThread().getTopFrameMethodInfo());
        RunDetector.backtrack();
    }

    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
        if (nextInsn instanceof InvokeInstruction) {
            InvokeInstruction invk = (InvokeInstruction) nextInsn;
            MethodInfo method = invk.getInvokedMethod();

            if (method != null) {
                if (debugMethods.containsKey(method.getFullName())) {
                    ExecuteInstructionHandler handler = debugMethods.get(method.getFullName());

                    handler.executeInstruction(vm, curTh, nextInsn);

                    curTh.skipInstruction(curTh.getPC().getNext());
                } else if (testMethods.containsKey(method.getFullName())) {
                    // Do not perform this action, instead call the handler
                    // This is needed to avoid an artificial INVOKE / RETURN to appear in the execution
                    // INVOKE and RETURN may break things
                    ExecuteInstructionHandler handler = testMethods.get(method.getFullName());

                    handler.executeInstruction(vm, curTh, nextInsn);

                    if (!vm.getSearch().isErrorState() || vm.getConfig().getBoolean("search.multiple_errors")) {
                        curTh.skipInstruction(curTh.getPC().getNext());
                    }
                }
            }
        }
    }

    @Override
    public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {
        RunDetector.detectRunning(vm, nextInsn, execInsn);

        if (RunDetector.isRunning() && nextInsn != null) {
            PredicateAbstraction.getInstance().getPredicateValuation().get(0).evaluateJustInScopePredicates(nextInsn.getPosition());
        }
    }

    @Override
    public void classLoaded(VM vm, ClassInfo classInfo) {
        PredicateAbstraction.getInstance().processNewClass(ThreadInfo.getCurrentThread(), classInfo);
    }

    @Override
    public void threadStarted(VM vm, ThreadInfo threadInfo) {
        PredicateAbstraction.getInstance().addThread(threadInfo);
    }

    @Override
    public void threadScheduled(VM vm, ThreadInfo threadInfo) {
        PredicateAbstraction.getInstance().scheduleThread(threadInfo);
    }

    @Override
    public void choiceGeneratorRegistered(VM vm, ChoiceGenerator<?> nextCG, ThreadInfo currentThread, Instruction executedInstruction) {
        if (!finished) {
            PredicateAbstraction.getInstance().collectGarbage(vm, currentThread);

            PredicateAbstraction.getInstance().rememberChoiceGenerator(nextCG);
        }
    }

    @Override
    public void choiceGeneratorAdvanced (VM vm, ChoiceGenerator<?> currentCG) {
        PredicateAbstraction.getInstance().rememberChoice(currentCG);
    }

    @Override
    public void choiceGeneratorProcessed (VM vm, ChoiceGenerator<?> currentCG) {
        PredicateAbstraction.getInstance().forgetChoiceGenerator();
    }

    private boolean finished = false;

    @Override
    public void searchFinished(Search search) {
        finished = true;

        Set<ExecuteInstructionHandler> handlers = new HashSet<ExecuteInstructionHandler>();

        handlers.addAll(testMethods.values());

        for (ExecuteInstructionHandler h : handlers) {
            h.searchFinished();
        }

        SMT.unregisterListeners();
        PredicateAbstraction.unregisterListeners();
    }
}
