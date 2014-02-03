package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.Map;
import java.util.HashMap;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class UniverseObject extends HeapValue implements Associative {
    private Map<FieldName, UniverseSlot> fields = new HashMap<FieldName, UniverseSlot>();

    public UniverseObject(ElementInfo elementInfo, ThreadInfo threadInfo) {
        super(new Reference(elementInfo, threadInfo));
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

}
