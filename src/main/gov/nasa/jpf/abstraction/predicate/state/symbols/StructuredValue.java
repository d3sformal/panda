package gov.nasa.jpf.abstraction.predicate.state.symbols;

import gov.nasa.jpf.vm.ElementInfo;

/**
 * Any value in the abstract heap that is not primitive (has fields/elements)
 */
public abstract class StructuredValue extends Value implements Comparable<StructuredValue> {

	private UniverseIdentifier reference;
	private ElementInfo elementInfo;
	
	protected StructuredValue(Universe universe, UniverseIdentifier reference, ElementInfo elementInfo) {
		super(universe);

		this.reference = reference;
		this.elementInfo = elementInfo;
	}
	
	public UniverseIdentifier getReference() {
		return reference;
	}
	
	public ElementInfo getElementInfo() {
		return elementInfo;
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

    protected abstract int compareSignatureTo(StructuredValue value);

    /**
     * Assumes same type of values (pair of objects / pair of arrays / pair of nulls)
     */
    protected abstract int compareSlots(StructuredValue value);

    protected final int compareClasses(StructuredValue value) {
        return getClass().getName().compareTo(value.getClass().getName());
    }

    @Override
    public int compareTo(StructuredValue value) {
        int sign = compareSignatureTo(value);

        if (sign != 0) return sign;

        return compareSlots(value);
    }
}
