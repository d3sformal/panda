package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class HeapValueSlot extends Slot {
	private Set<HeapValue> possibilities = new HashSet<HeapValue>();
	
	public HeapValueSlot(Value parent, Object slotKey, HeapValue... objects) {
		this(parent, slotKey, Arrays.asList(objects));
	}
	
	public HeapValueSlot(Value parent, Object slotKey, Collection<HeapValue> objects) {
		super(parent, slotKey);
		
		possibilities.addAll(objects);
		
		for (HeapValue object : objects) {
			object.addSlot(this);
		}
	}
	
	public Set<HeapValue> getPossibleHeapValues() {
		return possibilities;
	}
	
	@Override
	public Set<Value> getPossibleValues() {
		Set<Value> values = new HashSet<Value>();
		
		values.addAll(getPossibleHeapValues());
		
		return values;
	}

	@Override
	public HeapValueSlot cloneInto(Universe universe, Value parent) {
		HeapValueSlot clone = new HeapValueSlot(parent, getSlotKey());
		
		for (HeapValue value : possibilities) {
			clone.possibilities.add(value.cloneInto(universe, this));
		}
		
		return clone;
	}
	
	@Override
	public void clear() {
		for (HeapValue value : possibilities) {
			value.removeSlot(this);
		}
		
		possibilities.clear();
	}

	@Override
	public void add(Set<Value> sources) {
		for (Value value : sources) {
			value.addSlot(this);
			possibilities.add((HeapValue) value);
		}
	}
	
}
