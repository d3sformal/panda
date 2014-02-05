package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.HashSet;
import java.util.Set;

public class StructuredValueSlot extends AbstractUniverseSlot {
    private Set<StructuredValueIdentifier> possibleValues = new HashSet<StructuredValueIdentifier>();

    public StructuredValueSlot(Identifier parent, UniverseSlotKey slotKey) {
        super(parent, slotKey);
    }

    protected StructuredValueSlot() {
    }

    @Override
    public StructuredValueSlot createShallowCopy() {
        StructuredValueSlot copy = new StructuredValueSlot(getParent(), getSlotKey());

        copy.possibleValues.addAll(possibleValues);

        return copy;
    }

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
