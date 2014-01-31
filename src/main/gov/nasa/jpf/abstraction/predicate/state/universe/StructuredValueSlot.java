package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.HashSet;
import java.util.Set;

public class StructuredValueSlot extends AbstractUniverseSlot {
    private Set<StructuredValueIdentifier> possibleValues = new HashSet<StructuredValueIdentifier>();

    @Override
    public Set<? extends UniverseIdentifier> getPossibleValues() {
        return possibleValues;
    }

    @Override
    public void clear() {
        possibleValues.clear();
    }

    public Set<StructuredValueIdentifier> getPossibleStructuredValues() {
        return possibleValues;
    }

    public void addPossibleStructuredValue(StructuredValueIdentifier value) {
        possibleValues.add(value);
    }
}
