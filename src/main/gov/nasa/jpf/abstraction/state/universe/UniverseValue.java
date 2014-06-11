package gov.nasa.jpf.abstraction.state.universe;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.util.Pair;

public abstract class UniverseValue implements Freezable {
    protected boolean frozen = false;

    protected Set<Pair<Identifier, UniverseSlotKey>> parentSlots = new HashSet<Pair<Identifier, UniverseSlotKey>>();

    @Override
    public void freeze() {
        frozen = true;
    }

    @Override
    public boolean isFrozen() {
        return frozen;
    }

    @Override
    public abstract UniverseValue createShallowCopy();

    public Set<Pair<Identifier, UniverseSlotKey>> getParentSlots() {
        return parentSlots;
    }

    public void addParentSlot(Identifier parent, UniverseSlotKey slotKey) {
        parentSlots.add(new Pair<Identifier, UniverseSlotKey>(parent, slotKey));
    }

    public void removeParentSlot(Identifier parent, UniverseSlotKey slotKey) {
        parentSlots.remove(new Pair<Identifier, UniverseSlotKey>(parent, slotKey));
    }

    public abstract UniverseIdentifier getIdentifier();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object object);
}
