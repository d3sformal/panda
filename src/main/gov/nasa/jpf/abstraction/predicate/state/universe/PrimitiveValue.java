package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PrimitiveValue extends UniverseValue {
    private PrimitiveValueIdentifier identifier = new PrimitiveValueIdentifier();

    @Override
    public PrimitiveValue createShallowCopy() {
        return this; // No need to copy primitive values, they do not change nor get transferred
    }

    @Override
    public PrimitiveValueIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public int hashCode() {
        return getIdentifier().hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof PrimitiveValue) {
            return getIdentifier().equals(((PrimitiveValue) object).getIdentifier());
        }

        return false;
    }
}
