package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PrimitiveValueSlot extends Slot {

	private Set<PrimitiveValue> possibilities = new HashSet<PrimitiveValue>();
	
	public PrimitiveValueSlot(Value parent, Object slotKey, PrimitiveValue... primitives) {
		this(parent, slotKey, Arrays.asList(primitives));
	}
	
	public PrimitiveValueSlot(Value parent, Object slotKey, Collection<PrimitiveValue> primitives) {
		super(parent, slotKey);
		
		possibilities.addAll(primitives);
		
		for (PrimitiveValue primitive : primitives) {
			primitive.addSlot(this);
		}
	}
	
	public Set<PrimitiveValue> getPossiblePrimitiveValues() {
		return possibilities;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PrimitiveValueSlot) {
			PrimitiveValueSlot slot = (PrimitiveValueSlot) o;
			
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
		
		values.addAll(getPossiblePrimitiveValues());
		
		return values;
	}

	@Override
	public PrimitiveValueSlot cloneInto(Universe universe, Value parent) {
		PrimitiveValueSlot clone = new PrimitiveValueSlot(parent, getSlotKey());
		
		for (PrimitiveValue value : possibilities) {
			clone.possibilities.add(value.cloneInto(universe, clone));
		}
		
		return clone;
	}

	@Override
	public void clear() {
		for (PrimitiveValue value : possibilities) {
			value.removeSlot(this);
		}
		
		possibilities.clear();
	}

	@Override
	public void add(Set<Value> sources) {
		for (Value value : sources) {
			value.addSlot(this);
			possibilities.add((PrimitiveValue) value);
		}
	}

}