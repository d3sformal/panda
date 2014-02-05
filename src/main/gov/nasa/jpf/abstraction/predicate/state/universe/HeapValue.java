package gov.nasa.jpf.abstraction.predicate.state.universe;

public abstract class HeapValue extends StructuredValue {
    private Reference identifier;

    public HeapValue(Reference identifier) {
        this.identifier = identifier;
    }

    @Override
    public abstract HeapValue createShallowCopy();

    @Override
    public StructuredValueIdentifier getIdentifier() {
        return identifier;
    }

    public Reference getReference() {
        return identifier;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof HeapValue) {
            return identifier.equals(((HeapValue) object).identifier);
        }

        return false;
    }
}
