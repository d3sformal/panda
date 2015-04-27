package gov.nasa.jpf.abstraction;

import java.util.ArrayList;

import gov.nasa.jpf.vm.MethodInfo;

public class Method extends ArrayList<Integer> {
    private static final long serialVersionUID = 0L;
    private MethodInfo info;
    private TraceFormula trace;
    private TraceFormula.MethodBoundaries boundaries;

    public Method(MethodInfo info, TraceFormula trace, TraceFormula.MethodBoundaries b) {
        this.info = info;
        this.trace = trace;
        this.boundaries = b;
    }

    public MethodInfo getInfo() {
        return info;
    }

    public Step getStep(int i) {
        return trace.get(get(i));
    }

    public TraceFormula getTrace() {
        return trace;
    }

    public int getInit() {
        return boundaries.mCallInvoked;
    }

    public int getCall() {
        return boundaries.mCallStarted;
    }

    public int getReturn() {
        return boundaries.mReturn;
    }

    public int getFinish() {
        return boundaries.mReturned;
    }

    public Step getInitStep() {
        return trace.get(getInit());
    }

    public boolean equals(Object o) {
        if (o instanceof Method) {
            Method m = (Method) o;

            return info.equals(m.info) && boundaries == m.boundaries && trace == m.trace && super.equals(o);
        }

        return false;
    }
}
