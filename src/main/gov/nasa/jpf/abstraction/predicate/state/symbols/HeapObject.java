package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import gov.nasa.jpf.vm.ElementInfo;

/**
 * Java object residing in the heap
 */
public class HeapObject extends StructuredValue implements StructuredObject {
	
	private Map<String, Slot> fields = new HashMap<String, Slot>();

	public HeapObject(Universe universe, Integer reference, ElementInfo elementInfo) {
		super(universe, new HeapObjectReference(reference), elementInfo);
	}

    @Override
    protected final int compareSignatureTo(StructuredValue value) {
        if (value instanceof HeapObject) {
            HeapObject object = (HeapObject) value;

            String name1 = getElementInfo().getClassInfo().getName();
            String name2 = object.getElementInfo().getClassInfo().getName();

            return name1.compareTo(name2);
        }

        return compareClasses(value);
    }

    @Override
    protected final int compareSlots(StructuredValue value) {
        //HeapObject object = (HeapObject) value;

        return 0;
    }
	
	@Override
	public HeapObjectReference getReference() {
		return (HeapObjectReference) super.getReference();
	}

	@Override
	public void setField(String name, StructuredValue... values) {
		fields.put(name, new StructuredValueSlot(this, name, values));
	}
	
	@Override
	public void setField(String name, PrimitiveValue... values) {
		fields.put(name, new PrimitiveValueSlot(this, name, values));
	}
	
	@Override
	public Slot getField(String name) {
		return fields.get(name);
	}
	
	@Override
	public Map<String, Slot> getFields() {
		return fields;
	}

	@Override
	public HeapObject cloneInto(Universe universe, Slot slot) {		
		HeapObject clone = cloneInto(universe);
		
		clone.addSlot(slot);
		
		return clone;
	}
	
	@Override
	public HeapObject cloneInto(Universe universe) {
		boolean existed = universe.contains(getReference());
		
		HeapObjectReference reference = getReference();
		
		HeapObject clone = universe.getFactory().createObject(reference.getReference(), getElementInfo());
		
		if (!existed) {
			for (String field : fields.keySet()) {
				Slot slotClone = fields.get(field).cloneInto(universe, clone);
				
				clone.fields.put(field, slotClone);
			}
		}
		
		return clone;
	}

}
