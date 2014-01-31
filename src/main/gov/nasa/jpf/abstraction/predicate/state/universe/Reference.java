package gov.nasa.jpf.abstraction.predicate.state.universe;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class Reference implements StructuredValueIdentifier {
    private ElementInfo elementInfo;
    private ThreadInfo threadInfo;

    public Integer getReference() {
        return elementInfo == null ? Universe.NULL : elementInfo.getObjectRef();
    }
}
