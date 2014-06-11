package gov.nasa.jpf.abstraction.state.universe;

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

    public abstract void removeSlot(UniverseSlotKey slotKey);

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object object);
}
