package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.lang.Object;

import gov.nasa.jpf.abstraction.concrete.Reference;

public abstract class StructuredValue extends Value {
	private Reference reference;

	public StructuredValue(Reference reference) {
		this.reference = reference;
	}
	
	public Reference getReference() {
		return reference;
	}
	
	@Override
	public final boolean equals(Object o) {
		if (o instanceof StructuredValue) {
			return reference.equals(((StructuredValue) o).reference);
		}
		
		return false;
	}
	
	@Override
	public final int hashCode() {
		return reference.hashCode();
	}
	
	@Override
	public String toString() {
		return "ref(" + reference + ")";
	}
}
