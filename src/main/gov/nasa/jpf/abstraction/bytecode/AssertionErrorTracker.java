package gov.nasa.jpf.abstraction.bytecode;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.MethodInfo;

import gov.nasa.jpf.abstraction.util.Pair;

public class AssertionErrorTracker {
    private static Map<Integer, Pair<MethodInfo, Integer>> errorAllocationSite = new HashMap<Integer, Pair<MethodInfo, Integer>>();

    public static void setAssertionErrorAllocationSite(ElementInfo error, MethodInfo m, int pc) {
        errorAllocationSite.put(error.getObjectRef(), new Pair<MethodInfo, Integer>(m, pc));
    }

    public static Pair<MethodInfo, Integer> getAllocationSite(ElementInfo error) {
        return errorAllocationSite.get(error.getObjectRef());
    }
}
