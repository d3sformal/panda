package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.Set;

/**
 * Slots are used to model fields/elements/local variables etc.
 * 
 * They allow a non-deterministic assignment of a subvalue to a parent value.
 * It models situations like:
 * 
 * o.f = {}
 * o.f = {o1}
 * o.f = {o1, o2, ...}
 */
public abstract class Slot {
    /**
     * Each slot is contained in a value (object, array, local variable)
     */
	private Value parent;

    /**
     * Identifier of this slot within the object that contains it
     *
     * field name, array index, variable identifier (just for the sake of common interface)
     */
	private Object slotKey;
	
	protected Slot(Value parent, Object slotKey) {
		this.parent = parent;
		this.slotKey = slotKey;
	}
	
	public Value getParent() {
		return parent;
	}
	
    /**
     * Return the identifier of this particular slot: field name etc.
     */
	public Object getSlotKey() {
		return slotKey;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Slot) {
			Slot slot = (Slot) o;
			
			return parent.equals(slot.parent) && slotKey.equals(slot.slotKey);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return parent.hashCode() + (slotKey == null ? 0 : slotKey.hashCode());
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		
		ret.append(parent);
		
		if (parent instanceof StructuredObject) ret.append(".");
		if (parent instanceof StructuredArray) ret.append("[");
		if (parent instanceof StructuredValue) ret.append(slotKey);
		if (parent instanceof StructuredArray) ret.append("]");
		
		return ret.toString();
	}
	
	public int getSize() {
		return getPossibleValues().size();
	}
	
	public abstract Set<? extends Value> getPossibleValues();
	public abstract Slot cloneInto(Universe universe, Value parent);
	public abstract void clear();
	public abstract void add(Set<Value> sources);
}
