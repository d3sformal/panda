package gov.nasa.jpf.abstraction.util;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

/**
 * Prints the current symbol table after each instruction in the target program
 */
public class SymbolTableMonitor extends ListenerAdapter {

    @Override
    public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {
        if (RunDetector.isRunning()) {
            inspect(PredicateAbstraction.getInstance());
        }
    }

    private void inspect(Abstraction abs) {
        PredicateAbstraction predicate = (PredicateAbstraction) abs;
        String table = predicate.getSymbolTable().toString();
        System.out.println(
            "--SYMBOLS " + predicate.getSymbolTable().count() + " --\n" +
            table +
            "--------------"
        );
        System.out.flush();
    }
}
