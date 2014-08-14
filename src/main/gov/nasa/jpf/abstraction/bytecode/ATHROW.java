package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.util.Pair;

public class ATHROW extends gov.nasa.jpf.jvm.bytecode.ATHROW {
    @Override
    public Instruction execute(ThreadInfo ti) {
        StackFrame sf = ti.getTopFrame();
        ElementInfo ex = ti.getElementInfo(sf.peek());

        Pair<MethodInfo, Integer> allocSite = AssertionErrorTracker.getAllocationSite(ex);

        PredicateAbstraction.getInstance().cutTraceAfterAssertion(allocSite.getFirst(), allocSite.getSecond());

        return super.execute(ti);
    }
}
