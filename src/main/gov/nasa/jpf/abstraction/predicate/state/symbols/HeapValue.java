package gov.nasa.jpf.abstraction.predicate.state.symbols;

public abstract class HeapValue extends Value {
	private Integer reference;
	
	protected HeapValue(Integer reference) {
		this.reference = reference;
	}
	
	public Integer getReference() {
		return reference;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof HeapValue) {
			HeapValue value = (HeapValue) o;
			
			return reference == value.reference;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return reference;
	}
	
	@Override
	public String toString() {
		return "ref(" + reference + ")";
	}
}
