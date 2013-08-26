package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.HashSet;
import java.util.Set;

public abstract class Slot {
	private Set<Value> values = new HashSet<Value>();
	
	public enum Mode {
		AMBIGUOUS_WRITE,
		UNAMBIGUOUS_WRITE
	}
	
	public final Set<Value> getValues() {
		return values;
	}
	
	public final void write(Set<Value> fromValues) {
		for (Value value : fromValues) {
			value.addSlot(this);
		}
		
		values.addAll(fromValues);
	}
	
	public final void clear() {
		for (Value value : values) {
			value.removeSlot(this);
		}
			
		values.clear();
	}
}
