package gov.nasa.jpf.abstraction.predicate.state.universe;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class Reference implements StructuredValueIdentifier {
    private ElementInfo elementInfo;
    private ThreadInfo threadInfo;

    public Reference(ElementInfo elementInfo, ThreadInfo threadInfo) {
        this.elementInfo = elementInfo;
        this.threadInfo = threadInfo;
    }

    public Integer getReference() {
        return elementInfo == null ? Universe.NULL : elementInfo.getObjectRef();
    }

    public ElementInfo getElementInfo() {
        return elementInfo;
    }

    public ThreadInfo getThreadInfo() {
        return threadInfo;
    }

    @Override
    public int hashCode() {
        return getReference();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Reference) {
            return getReference().equals(((Reference) object).getReference());
        }

        return false;
    }

    @Override
    public String toString() {
        return getReference().toString();
    }

}
