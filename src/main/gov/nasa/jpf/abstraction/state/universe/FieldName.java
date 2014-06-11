package gov.nasa.jpf.abstraction.state.universe;

public class FieldName implements UniverseSlotKey, Comparable<FieldName> {
    private String name;

    public FieldName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof FieldName) {
            return name.equals(((FieldName) object).name);
        }

        return false;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(FieldName f) {
        return getName().compareTo(f.getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
