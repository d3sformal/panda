package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.Map;
import java.util.HashMap;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class UniverseArray extends HeapValue implements Indexed {
    private Map<ElementIndex, UniverseSlot> elements = new HashMap<ElementIndex, UniverseSlot>();
    private Integer length;

    public UniverseArray(ElementInfo elementInfo, ThreadInfo threadInfo) {
        super(new Reference(elementInfo, threadInfo));

        length = elementInfo.arrayLength();
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
    public Integer getLength() {
        return length;
    }

    @Override
    public void setElement(ElementIndex index, UniverseIdentifier value) {
        if (elements.containsKey(index)) throw new RuntimeException("Redefinition of an element [" + index + "]");

        UniverseSlot slot = null;

        if (value instanceof StructuredValueIdentifier) {
            StructuredValueSlot sslot = new StructuredValueSlot();

            sslot.addPossibleStructuredValue((StructuredValueIdentifier) value);

            slot = sslot;
        }

        if (value instanceof PrimitiveValueIdentifier) {
            PrimitiveValueSlot pslot = new PrimitiveValueSlot();

            pslot.addPossiblePrimitiveValue((PrimitiveValueIdentifier) value);

            slot = pslot;
        }

        elements.put(index, slot);
    }

}
