package gov.nasa.jpf.abstraction.state.universe;

public class ElementIndex implements UniverseSlotKey, Comparable<ElementIndex> {
    private int index;

    public ElementIndex(int index) {
        this.index = index;
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ElementIndex) {
            return index == ((ElementIndex) object).index;
        }

        return false;
    }

    public Integer getIndex() {
        return index;
    }

    @Override
    public int compareTo(ElementIndex i) {
        return getIndex().compareTo(i.getIndex());
    }

    @Override
    public String toString() {
        return getIndex().toString();
    }
}
