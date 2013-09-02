package gov.nasa.jpf.abstraction.predicate.state.symbols;

public class HeapObjectReference implements UniverseIdentifier {
	private int reference;
	
	public HeapObjectReference(Integer reference) {
		this.reference = reference;
	}
	
	public Integer getReference() {
		return reference;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof HeapObjectReference) {
			HeapObjectReference r = (HeapObjectReference) o;
			
			return reference == r.reference;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return reference;
	}
	
	@Override
	public String toString() {
		return ((Integer) reference).toString();
	}
}
