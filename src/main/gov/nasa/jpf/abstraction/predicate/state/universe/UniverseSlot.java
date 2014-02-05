package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.Set;

public interface UniverseSlot extends Freezable {
    @Override
    public UniverseSlot createShallowCopy();

    public Identifier getParent();
    public void setParent(Identifier identifier);
    public UniverseSlotKey getSlotKey();
    public void setSlotKey(UniverseSlotKey slotKey);
    public Set<? extends UniverseIdentifier> getPossibleValues();
    public void clear();
}
