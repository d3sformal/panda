package gov.nasa.jpf.abstraction.state.universe;

import gov.nasa.jpf.vm.ElementInfo;

public class Reference implements StructuredValueIdentifier {
    private ElementInfo elementInfo;

    public Reference(ElementInfo elementInfo) {
        this.elementInfo = elementInfo;
    }

    public Integer getReferenceNumber() {
        return elementInfo == null ? Universe.NULL : elementInfo.getObjectRef();
    }

    public ElementInfo getElementInfo() {
        return elementInfo;
    }

    @Override
    public int hashCode() {
        return getReferenceNumber();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Reference) {
            return getReferenceNumber().equals(((Reference) object).getReferenceNumber());
        }

        return false;
    }

    @Override
    public String toString() {
        return getReferenceNumber().toString();
    }

    @Override
    public int compareTo(Identifier id) {
        if (id instanceof Reference) {
            Reference ref = (Reference) id;

            if (getElementInfo() == null) return -1;
            if (ref.getElementInfo() == null) return +1;

            int classComparison = getElementInfo().getClassInfo().getName().compareTo(ref.getElementInfo().getClassInfo().getName());

            if (classComparison == 0) {
                return getReferenceNumber().compareTo(ref.getReferenceNumber());
            }

            return classComparison;
        }

        return Identifier.Ordering.compare(this, id);
    }

}
