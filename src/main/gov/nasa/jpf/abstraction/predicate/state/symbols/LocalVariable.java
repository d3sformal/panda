package gov.nasa.jpf.abstraction.predicate.state.symbols;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;

public class LocalVariable extends Value {
	private Root localVariable;
	private Slot slot;
	
	protected LocalVariable() {
	}
	
	public LocalVariable(Root localVariable, Value value) {
		this.localVariable = localVariable;
		
		if (value instanceof PrimitiveValue) {
			slot = new PrimitiveValueSlot(this, localVariable.getName(), (PrimitiveValue)value);
		}
		
		if (value instanceof StructuredValue) {
			slot = new StructuredValueSlot(this, localVariable.getName(), (StructuredValue)value);
		}
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
		LocalVariable clone = new LocalVariable();
		
		clone.localVariable = localVariable.clone();
		clone.slot = slot.cloneInto(universe, this);
		
		return clone;
	}

	@Override
	public LocalVariable cloneInto(Universe universe, Slot slot) {
		return cloneInto(universe);
	}
}
