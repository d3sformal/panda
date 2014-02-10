package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.Map;
import java.util.HashMap;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.util.Pair;

public class UniverseArray extends HeapValue implements Indexed {
    private Map<ElementIndex, UniverseSlot> elements = new HashMap<ElementIndex, UniverseSlot>();
    private Integer length;

    public UniverseArray(ElementInfo elementInfo) {
        super(new Reference(elementInfo));

        length = elementInfo.arrayLength();
    }

    protected UniverseArray(Reference identifier, Integer length) {
        super(identifier);

        this.length = length;
    }

    @Override
    public UniverseArray createShallowCopy() {
        UniverseArray copy = new UniverseArray(getReference(), getLength());

        for (Pair<Identifier, UniverseSlotKey> parentSlot : getParentSlots()) {
            copy.addParentSlot(parentSlot.getFirst(), parentSlot.getSecond());
        }

        copy.elements.putAll(elements);

        return copy;
    }

    @Override
    public UniverseSlot getSlot(UniverseSlotKey key) {
        return elements.get((ElementIndex) key);
    }

    @Override
    public Map<? extends UniverseSlotKey, UniverseSlot> getSlots() {
        return elements;
    }

    @Override
    public UniverseSlot getElement(ElementIndex index) {
        return elements.get(index);
    }

    @Override
    public Map<ElementIndex, UniverseSlot> getElements() {
        return elements;
    }

    @Override
    public void addSlot(UniverseSlotKey slotKey, UniverseSlot slot) {
        elements.put((ElementIndex) slotKey, slot);
    }

    @Override
    public void removeSlot(UniverseSlotKey slotKey) {
        elements.remove((ElementIndex) slotKey);
    }

    @Override
    public Integer getLength() {
        return length;
    }

}
