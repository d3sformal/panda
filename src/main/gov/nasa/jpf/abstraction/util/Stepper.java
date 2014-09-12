package gov.nasa.jpf.abstraction.util;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class Stepper extends ListenerAdapter {
    @Override
    public void executeInstruction(VM vm, ThreadInfo currentThread, Instruction instructionToExecute) {
        if (RunDetector.isRunning()) {
            try {
                System.out.println("Press ENTER to continue");
                System.in.read();
            } catch (Exception e) {
            }
        }
    }
}
