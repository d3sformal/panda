package gov.nasa.jpf.abstraction;

import java.util.ArrayList;

import gov.nasa.jpf.vm.MethodInfo;

public class Method extends ArrayList<Integer> {
    private static final long serialVersionUID = 0L;
    private MethodInfo info;
    private TraceFormula trace;
    private int init;

    public Method(MethodInfo info, TraceFormula trace, Integer init) {
        this.info = info;
        this.trace = trace;
        this.init = init;
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
        return init;
    }

    public Step getInitStep() {
        return trace.get(getInit());
    }

    public boolean equals(Object o) {
        if (o instanceof Method) {
            Method m = (Method) o;

            return info.equals(m.info) && init == m.init && trace == m.trace && super.equals(o);
        }

        return false;
    }
}
