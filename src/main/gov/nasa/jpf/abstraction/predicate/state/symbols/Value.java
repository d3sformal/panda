package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.HashSet;
import java.util.Set;

/**
 * Common ancestor of all the values stored in the memory representation
 *
 * Objects / Arrays ...
 */
public abstract class Value {
    /**
     * Slots (fields, array elements) that this value may be member of
     */
	private Set<Slot> slots = new HashSet<Slot>();
	private Universe universe;
	
	public Value(Universe universe) {
		this.universe = universe;
	}

	public final void addSlot(Slot slot) {
		slots.add(slot);
	}
	
	public final Set<Slot> getSlots() {
		return slots;
	}
	
	public final void removeSlot(Slot slot) {
		slots.remove(slot);
	}
	
	public final int getUniverseID() {
		return universe.getID();
	}

	public abstract Value cloneInto(Universe universe);
	public abstract Value cloneInto(Universe universe, Slot slot);
}
