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
	
}
