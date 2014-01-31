package gov.nasa.jpf.abstraction.predicate.state.universe;

public abstract class AbstractUniverseSlot implements UniverseSlot {
    protected boolean frozen = false;
    private Identifier parent;
    private UniverseSlotKey slotKey;

    @Override
    public void freeze() {
        frozen = true;
    }

    @Override
    public Identifier getParent() {
        return parent;
    }

    @Override
    public UniverseSlotKey getSlotKey() {
        return slotKey;
    }
}
