package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.UUID;

/**
 * Represents numerical memory slots in the abstract heap
 */
public class PrimitiveValue extends Value {
	
	private UUID id;
	
	public PrimitiveValue(Universe universe) {
		super(universe);

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
		PrimitiveValue clone = new PrimitiveValue(universe);
		
		clone.id = id;
				
		return clone;
	}
}
