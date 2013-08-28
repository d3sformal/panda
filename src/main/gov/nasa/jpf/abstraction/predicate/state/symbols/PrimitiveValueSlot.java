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
	public Set<Value> getPossibleValues() {
		Set<Value> values = new HashSet<Value>();
		
		values.addAll(getPossiblePrimitiveValues());
		
		return values;
	}

}
