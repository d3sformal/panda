package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class HeapValueSlot extends Slot {
	private Set<HeapValue> possibilities = new HashSet<HeapValue>();
	
	public HeapValueSlot(HeapValue parent, Object slotKey, HeapValue... objects) {
		this(parent, slotKey, Arrays.asList(objects));
	}
	
	public HeapValueSlot(HeapValue parent, Object slotKey, Collection<HeapValue> objects) {
		super(parent, slotKey);
		
		possibilities.addAll(objects);
		
		for (HeapValue object : objects) {
			object.addSlot(this);
		}
	}
	
	public Set<HeapValue> getPossibilities() {
		return possibilities;
	}
}
