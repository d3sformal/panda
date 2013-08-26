package gov.nasa.jpf.abstraction.predicate.state.symbols;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Value {
	
	private Set<Slot> slots = new HashSet<Slot>();
	
	public final void addSlot(Slot slot) {
		slots.add(slot);
	}
	
	public final void removeSlot(Slot slot) {
		slots.remove(slot);
	}
	
	public final Set<Slot> getSlots() {
		return slots;
	}
	
	public abstract void build(int max);
	public abstract Map<AccessExpression, Set<Value>> resolve(AccessExpression prefix, int max);
	
}
