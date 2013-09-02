package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ClassStaticsSlot extends Slot {
	private Set<ClassStatics> possibilities = new HashSet<ClassStatics>();
	
	public ClassStaticsSlot(Value parent, Object slotKey, ClassStatics... objects) {
		this(parent, slotKey, Arrays.asList(objects));
	}
	
	public ClassStaticsSlot(Value parent, Object slotKey, Collection<ClassStatics> objects) {
		super(parent, slotKey);
		
		possibilities.addAll(objects);
		
		for (ClassStatics object : objects) {
			object.addSlot(this);
		}
	}
	
	public Set<ClassStatics> getPossibleHeapValues() {
		return possibilities;
	}
	
	@Override
	public Set<Value> getPossibleValues() {
		Set<Value> values = new HashSet<Value>();
		
		values.addAll(getPossibleHeapValues());
		
		return values;
	}

	@Override
	public ClassStaticsSlot cloneInto(Universe universe, Value parent) {
		ClassStaticsSlot clone = new ClassStaticsSlot(parent, getSlotKey());
		
		for (ClassStatics value : possibilities) {
			clone.possibilities.add(value.cloneInto(universe, this));
		}
		
		return clone;
	}
	
	@Override
	public void clear() {
		for (ClassStatics value : possibilities) {
			value.removeSlot(this);
		}
		
		possibilities.clear();
	}

	@Override
	public void add(Set<Value> sources) {
		for (Value value : sources) {
			value.addSlot(this);
			possibilities.add((ClassStatics) value);
		}
	}
}
