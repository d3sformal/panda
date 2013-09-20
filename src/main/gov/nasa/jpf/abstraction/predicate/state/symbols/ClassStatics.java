package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.HashMap;
import java.util.Map;

/**
 * Collection of static fields of a given class
 */
public class ClassStatics extends StructuredValue implements StructuredObject {

	private Map<String, Slot> fields = new HashMap<String, Slot>();

	public ClassStatics(Universe universe, String className) {
		super(universe, new ClassStaticsReference(className));
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
	public ClassStatics cloneInto(Universe universe, Slot slot) {		
		ClassStatics clone = cloneInto(universe);
		
		clone.addSlot(slot);
		
		return clone;
	}
	
	@Override
	public ClassStatics cloneInto(Universe universe) {
		boolean existed = universe.contains(getReference());
		
		ClassStaticsReference reference = (ClassStaticsReference) getReference();
		
		ClassStatics clone = universe.getFactory().createClass(reference.getClassName());
		
		if (!existed) {
			for (String field : fields.keySet()) {
				Slot slotClone = fields.get(field).cloneInto(universe, clone);
				
				clone.fields.put(field, slotClone);
			}
		}
		
		return clone;
	}
	
	@Override
	public String toString() {
		return getReference().toString();
	}
}
