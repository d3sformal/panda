package gov.nasa.jpf.abstraction.predicate.state.symbols;

public abstract class HeapValue extends StructuredValue {
	
	protected HeapValue(HeapObjectReference reference) {
		super(reference);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof HeapValue) {
			HeapValue value = (HeapValue) o;
			
			return getReference().equals(value.getReference());
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return getReference().hashCode();
	}
	
	@Override
	public String toString() {
		return "ref(" + getReference() + ")";
	}
	
	@Override
	public abstract HeapValue cloneInto(Universe universe, Slot slot);
	
	@Override
	public abstract HeapValue cloneInto(Universe universe);
}
