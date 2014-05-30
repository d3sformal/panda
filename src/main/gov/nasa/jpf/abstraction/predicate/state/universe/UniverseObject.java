package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.Map;
import java.util.HashMap;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.util.Pair;

public class UniverseObject extends HeapValue implements Associative {
    private Map<FieldName, UniverseSlot> fields = new HashMap<FieldName, UniverseSlot>();

    public UniverseObject(ElementInfo elementInfo) {
        super(new Reference(elementInfo));
    }

    protected UniverseObject(Reference identifier) {
        super(identifier);
    }

    @Override
    public UniverseObject createShallowCopy() {
        UniverseObject copy = new UniverseObject(getReference());

        for (Pair<Identifier, UniverseSlotKey> parentSlot : getParentSlots()) {
            copy.addParentSlot(parentSlot.getFirst(), parentSlot.getSecond());
        }

        copy.fields.putAll(fields);

        return copy;
    }

    @Override
    public UniverseSlot getSlot(UniverseSlotKey key) {
        return fields.get((FieldName) key);
    }

    @Override
    public Map<? extends FieldName, UniverseSlot> getSlots() {
        return fields;
    }

    @Override
    public UniverseSlot getField(FieldName name) {
        return fields.get(name);
    }

    @Override
    public Map<FieldName, UniverseSlot> getFields() {
        return fields;
    }

    @Override
    public void addSlot(UniverseSlotKey slotKey, UniverseSlot slot) {
        fields.put((FieldName) slotKey, slot);
    }

    @Override
    public void removeSlot(UniverseSlotKey slotKey) {
        fields.remove((FieldName) slotKey);
    }

}
