package gov.nasa.jpf.abstraction.predicate.state.symbols;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;

/**
 * Models local variables
 */
public class LocalVariable extends Value {
	private Root localVariable;
	private Slot slot;
	
	protected LocalVariable(Universe universe) {
		super(universe);
	}
	
	public LocalVariable(Universe universe, Root localVariable, Value value) {
		this(universe);

		this.localVariable = localVariable;
		
		if (value instanceof PrimitiveValue) {
			slot = new PrimitiveValueSlot(this, localVariable.getName(), (PrimitiveValue)value);
			return;
		}
		
		if (value instanceof StructuredValue) {
			slot = new StructuredValueSlot(this, localVariable.getName(), (StructuredValue)value);
			return;
		}
		
		throw new RuntimeException("Unknown type of value: " + value);
	}
	
	public AccessExpression getAccessExpression() {
		return localVariable;
	}
	
	public Slot getSlot() {
		return slot;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof LocalVariable) {
			LocalVariable l = (LocalVariable) o;
			
			return localVariable.equals(l.localVariable);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return localVariable.hashCode();
	}
	
	@Override
	public String toString() {
		return localVariable.getName();
	}

	@Override
	public LocalVariable cloneInto(Universe universe) {
		LocalVariable clone = new LocalVariable(universe);
		
		clone.localVariable = localVariable;
		clone.slot = slot.cloneInto(universe, clone);
		
		return clone;
	}

	@Override
	public LocalVariable cloneInto(Universe universe, Slot slot) {
		return cloneInto(universe);
	}
}
