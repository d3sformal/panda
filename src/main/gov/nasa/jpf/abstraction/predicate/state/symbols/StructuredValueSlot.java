package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class StructuredValueSlot extends Slot {
	private Set<StructuredValue> possibilities = new HashSet<StructuredValue>();
	
	public StructuredValueSlot(Value parent, Object slotKey, StructuredValue... objects) {
		this(parent, slotKey, Arrays.asList(objects));
	}
	
	public StructuredValueSlot(Value parent, Object slotKey, Collection<StructuredValue> objects) {
		super(parent, slotKey);
		
		possibilities.addAll(objects);
		
		for (StructuredValue object : objects) {
			object.addSlot(this);
		}
	}
	
	public Set<StructuredValue> getPossibleHeapValues() {
		return possibilities;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof StructuredValueSlot) {
			StructuredValueSlot slot = (StructuredValueSlot) o;
			
			return getParent().equals(slot.getParent()) && getSlotKey().equals(slot.getSlotKey());
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return getParent().hashCode() + getSlotKey().hashCode();
	}
	
	@Override
	public Set<Value> getPossibleValues() {
		Set<Value> values = new HashSet<Value>();
		
		values.addAll(getPossibleHeapValues());
		
		return values;
	}

	@Override
	public StructuredValueSlot cloneInto(Universe universe, Value parent) {
		StructuredValueSlot clone = new StructuredValueSlot(parent, getSlotKey());
		
		for (StructuredValue value : possibilities) {
			clone.possibilities.add(value.cloneInto(universe, clone));
		}
		
		return clone;
	}
	
	@Override
	public void clear() {
		for (StructuredValue value : possibilities) {
			value.removeSlot(this);
		}
		
		possibilities.clear();
	}

	@Override
	public void add(Set<Value> sources) {
		for (Value value : sources) {
			value.addSlot(this);
			possibilities.add((StructuredValue) value);
		}
	}
	
}
