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
		fields.put(name, new PrimitiveValueSlot(this, name));
	}
	
	public Slot getField(String name) {
		return fields.get(name);
	}
	
	public Map<String, Slot> getFields() {
		return fields;
	}

}
