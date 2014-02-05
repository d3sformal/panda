package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class StructuredValue extends UniverseValue {
    @Override
    public abstract StructuredValue createShallowCopy();

    @Override
    public abstract StructuredValueIdentifier getIdentifier();

    public abstract UniverseSlot getSlot(UniverseSlotKey key);

    public abstract Map<? extends UniverseSlotKey, ? extends UniverseSlot> getSlots();

    public abstract void addSlot(UniverseSlotKey slotKey, UniverseSlot slot);

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object object);
}
