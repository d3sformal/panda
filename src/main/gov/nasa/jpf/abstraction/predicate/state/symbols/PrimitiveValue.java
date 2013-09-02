package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.UUID;

public class PrimitiveValue extends Value {
	
	//TODO identifier of the current value
	// 1) method m modifies a primitive value reachable from parent scope
	// 2) return
	// 3) need to detect the modification - comparison of the universes - need to distinguish the primitive values
	private UUID id;
	
	public PrimitiveValue() {
		id = UUID.randomUUID();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PrimitiveValue) {
			PrimitiveValue value = (PrimitiveValue) o;
			
			return id.equals(value.id);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public String toString() {
		return "primitive_" + id;
	}

	@Override
	public PrimitiveValue cloneInto(Universe universe, Slot slot) {
		PrimitiveValue clone = cloneInto(universe);
		
		clone.addSlot(slot);
		
		return clone;
	}

	@Override
	public PrimitiveValue cloneInto(Universe universe) {
		PrimitiveValue clone = new PrimitiveValue();
		
		clone.id = id;
				
		return clone;
	}
}
