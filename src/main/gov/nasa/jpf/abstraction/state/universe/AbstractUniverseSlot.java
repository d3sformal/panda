package gov.nasa.jpf.abstraction.state.universe;

public abstract class AbstractUniverseSlot implements UniverseSlot {
    protected boolean frozen = false;
    private Identifier parent;
    private UniverseSlotKey slotKey;

    public AbstractUniverseSlot(Identifier parent, UniverseSlotKey slotKey) {
        this.parent = parent;
        this.slotKey = slotKey;
    }

    protected AbstractUniverseSlot() {
    }

    @Override
    public void freeze() {
        frozen = true;
    }

    @Override
    public boolean isFrozen() {
        return frozen;
    }

    @Override
    public abstract AbstractUniverseSlot createShallowCopy();

    @Override
    public Identifier getParent() {
        return parent;
    }

    @Override
    public void setParent(Identifier parent) {
        this.parent = parent;
    }

    @Override
    public UniverseSlotKey getSlotKey() {
        return slotKey;
    }

    @Override
    public void setSlotKey(UniverseSlotKey slotKey) {
        this.slotKey = slotKey;
    }
}
