package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.HashMap;
import java.util.Map;

public class HeapObject extends HeapValue {
	
	private Map<String, Slot> fields = new HashMap<String, Slot>();

	public HeapObject(Integer reference) {
		super(reference);
	}
	
	public void setField(String name, HeapValue... values) {
		fields.put(name, new HeapValueSlot(this, name, values));
	}
	
	public void setField(String name, PrimitiveValue... values) {
		fields.put(name, new PrimitiveValueSlot(this, name, values));
	}
	
	public Slot getField(String name) {
		return fields.get(name);
	}
	
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
		
		HeapObject clone = universe.getFactory().createObject(getReference());
		
		if (!existed) {
			for (String field : fields.keySet()) {
				Slot slotClone = fields.get(field).cloneInto(universe, clone);
				
				clone.fields.put(field, slotClone);
			}
		}
		
		return clone;
	}

}
