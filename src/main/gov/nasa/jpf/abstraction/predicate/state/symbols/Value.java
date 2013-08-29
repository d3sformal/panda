package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.HashSet;
import java.util.Set;

public abstract class Value {
	private Set<Slot> slots = new HashSet<Slot>();
	
	public void addSlot(Slot slot) {
		slots.add(slot);
	}
	
	public Set<Slot> getSlots() {
		return slots;
	}
	
	public void removeSlot(Slot slot) {
		slots.remove(slot);
	}
	
	public abstract Value cloneInto(Universe universe);
	public abstract Value cloneInto(Universe universe, Slot slot);
}
