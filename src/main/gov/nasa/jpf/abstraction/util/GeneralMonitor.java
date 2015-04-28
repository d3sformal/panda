package gov.nasa.jpf.abstraction.util;

import java.io.PrintStream;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.smt.SMT;

public class GeneralMonitor extends ListenerAdapter {
    private PrintStream out;

    private static final String erase = "[1J[H";

    public GeneralMonitor() {
        out = BacktrackedLogger.getOriginalStdOut();
    }

    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction execInsn) {
        out.println(erase + "sat: " + SMT.getIsSat() + " itp: " + SMT.getItp() + " elapsed: " + (SMT.getElapsed() / 1000) + "s");
    }
}
