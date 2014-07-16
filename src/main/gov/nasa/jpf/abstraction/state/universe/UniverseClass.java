package gov.nasa.jpf.abstraction.state.universe;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.vm.StaticElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.util.Pair;

public class UniverseClass extends StructuredValue implements Associative {
    private ClassName identifier;
    private Map<FieldName, UniverseSlot> fields = new HashMap<FieldName, UniverseSlot>();

    public UniverseClass(StaticElementInfo elementInfo) {
        identifier = new ClassName(elementInfo);
    }

    protected UniverseClass(ClassName identifier) {
        this.identifier = identifier;
    }

    public ClassName getClassName() {
        return identifier;
    }

    @Override
    public UniverseClass createShallowCopy() {
        UniverseClass copy = new UniverseClass(identifier);

        for (Pair<Identifier, UniverseSlotKey> parentSlot : getParentSlots()) {
            copy.addParentSlot(parentSlot.getFirst(), parentSlot.getSecond());
        }

        copy.fields.putAll(fields);

        return copy;
    }

    @Override
    public StructuredValueIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public UniverseSlot getSlot(UniverseSlotKey key) {
        return fields.get((FieldName) key);
    }

    @Override
    public Map<? extends UniverseSlotKey, UniverseSlot> getSlots() {
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

    @Override
    public UniverseSlot getField(FieldName name) {
        return fields.get(name);
    }

    @Override
    public Map<FieldName, UniverseSlot> getFields() {
        return fields;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof UniverseClass) {
            return identifier.equals(((UniverseClass) object).identifier);
        }

        return false;
    }
}
