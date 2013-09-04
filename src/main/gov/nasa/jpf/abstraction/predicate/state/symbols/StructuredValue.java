package gov.nasa.jpf.abstraction.predicate.state.symbols;

public abstract class StructuredValue extends Value {
	
	private UniverseIdentifier reference;
	
	protected StructuredValue(UniverseIdentifier reference) {
		this.reference = reference;
	}
	
	public UniverseIdentifier getReference() {
		return reference;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof StructuredValue) {
			StructuredValue value = (StructuredValue) o;
			
			return reference.equals(value.reference);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return reference.hashCode();
	}
	
	@Override
	public String toString() {
		return "ref(" + reference + ")";
	}
	
	@Override
	public abstract StructuredValue cloneInto(Universe universe, Slot slot);
	
	@Override
	public abstract StructuredValue cloneInto(Universe universe);
}