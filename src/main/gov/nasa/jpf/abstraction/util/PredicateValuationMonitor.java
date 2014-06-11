package gov.nasa.jpf.abstraction.util;

import java.util.Stack;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ReturnInstruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

/**
 * Prints the current predicate valuation after each instruction in the target program
 */
public class PredicateValuationMonitor extends ListenerAdapter {

    @Override
    public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {
        if (RunDetector.isRunning()) {
            inspect(PredicateAbstraction.getInstance());
        }
    }

    private void inspect(Abstraction abs) {
        PredicateAbstraction predicate = (PredicateAbstraction) abs;
        String table = predicate.getPredicateValuation().toString();
        System.out.println(
            "--PREDICATES " + predicate.getPredicateValuation().count() + " --\n" +
            table +
            "--------------"
        );
        System.out.flush();
    }
}
