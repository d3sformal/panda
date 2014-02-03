package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.Map;
import java.util.HashMap;

import gov.nasa.jpf.vm.StaticElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class UniverseClass extends UniverseStructuredValue implements Associative {
    private ClassName identifier;
    private Map<FieldName, UniverseSlot> fields = new HashMap<FieldName, UniverseSlot>();

    public UniverseClass(StaticElementInfo elementInfo, ThreadInfo threadInfo) {
        identifier = new ClassName(elementInfo, threadInfo);
    }

    public ClassName getClassName() {
        return identifier;
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
    public UniverseSlot getField(FieldName name) {
        return fields.get(name);
    }

    @Override
    public Map<FieldName, UniverseSlot> getFields() {
        return fields;
    }

    @Override
    public void setField(FieldName name, UniverseIdentifier value) {
        if (fields.containsKey(name)) throw new RuntimeException("Redefinition of a field `" + name + "`");

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

        fields.put(name, slot);
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

