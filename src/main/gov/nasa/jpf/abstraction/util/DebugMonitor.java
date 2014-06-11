package gov.nasa.jpf.abstraction.util;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;


public class DebugMonitor extends ListenerAdapter {
    public DebugMonitor(Config cfg, JPF jpf) {
    }

    @Override
    public void stateAdvanced(Search search) {
        System.out.print("[MONITOR] state : ");

        if (search.isNewState()) {
            System.out.print("new");
        } else {
            System.out.print("visited");
        }

        System.out.println(" , id = " + search.getStateId());
    }

    @Override
    public void stateBacktracked(Search search) {
        System.out.println("[MONITOR] backtrack");
    }
}
