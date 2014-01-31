package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.Set;

public interface UniverseSlot {
    public void freeze();
    public Identifier getParent();
    public UniverseSlotKey getSlotKey();
    public Set<? extends UniverseIdentifier> getPossibleValues();
    public void clear();
}
