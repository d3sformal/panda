package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.HashSet;
import java.util.Set;

public class PrimitiveValueSlot extends AbstractUniverseSlot {
    private Set<PrimitiveValueIdentifier> possibleValues = new HashSet<PrimitiveValueIdentifier>();

    @Override
    public Set<? extends UniverseIdentifier> getPossibleValues() {
        return possibleValues;
    }

    @Override
    public void clear() {
        possibleValues.clear();
    }

    public Set<PrimitiveValueIdentifier> getPossiblePrimitiveValues() {
        return possibleValues;
    }

    public void addPossiblePrimitiveValue(PrimitiveValueIdentifier value) {
        possibleValues.add(value);
    }
}
