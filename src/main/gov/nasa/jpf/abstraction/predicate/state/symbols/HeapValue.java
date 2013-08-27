package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.HashSet;
import java.util.Set;

public abstract class HeapValue implements Value {
	private Set<Slot> slots = new HashSet<Slot>();
	private Integer reference;
	
	protected HeapValue(Integer reference) {
		this.reference = reference;
	}
	
	public Integer getReference() {
		return reference;
	}
	
	public void addSlot(Slot slot) {
		slots.add(slot);
	}
	
	public Set<Slot> getSlots() {
		return slots;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof HeapValue) {
			HeapValue value = (HeapValue) o;
			
			return reference == value.reference;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return reference;
	}
	
	@Override
	public String toString() {
		return "ref(" + reference + ")";
	}
}
