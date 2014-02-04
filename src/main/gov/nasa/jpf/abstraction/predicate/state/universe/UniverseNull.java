package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.Map;
import java.util.Collections;

public class UniverseNull extends HeapValue implements Associative, Indexed {

    public static Reference nullReference = new Reference(null, null);

    public UniverseNull() {
        super(nullReference);
    }

    @Override
    public UniverseSlot getSlot(UniverseSlotKey key) {
        throw new RuntimeException("Trying to access a slot of a NULL object");
    }

    @Override
    public Map<? extends UniverseSlotKey, UniverseSlot> getSlots() {
        return Collections.emptyMap();
    }

    @Override
    public UniverseSlot getField(FieldName name) {
        throw new RuntimeException("Trying to access a field of a NULL object");
    }

    @Override
    public Map<FieldName, UniverseSlot> getFields() {
        return Collections.emptyMap();
    }

    @Override
    public UniverseSlot getElement(ElementIndex index) {
        throw new RuntimeException("Trying to access an element of a NULL array");
    }

    @Override
    public Map<ElementIndex, UniverseSlot> getElements() {
        return Collections.emptyMap();
    }

    @Override
    public void addSlot(UniverseSlotKey slotKey, UniverseSlot slot) {
        throw new RuntimeException("Trying to add a slot to a NULL object");
    }

    @Override
    public Integer getLength() {
        throw new RuntimeException("Trying to access the length of a NULL array");
    }
}
