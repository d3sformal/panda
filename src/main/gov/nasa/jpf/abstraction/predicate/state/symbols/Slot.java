package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.Set;

public abstract class Slot {
	private Value parent;
	private Object slotKey;
	
	protected Slot(Value parent, Object slotKey) {
		this.parent = parent;
		this.slotKey = slotKey;
	}
	
	public Value getParent() {
		return parent;
	}
	
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
		
		ret.append(slotKey);
		
		if (parent instanceof StructuredArray) ret.append("]");
		
		return ret.toString();
	}
	
	public int getSize() {
		return getPossibleValues().size();
	}
	
	public abstract Set<Value> getPossibleValues();
	public abstract Slot cloneInto(Universe universe, Value parent);
	public abstract void clear();
	public abstract void add(Set<Value> sources);
}
