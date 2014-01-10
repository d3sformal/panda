package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import gov.nasa.jpf.vm.ElementInfo;

/**
 * Java array residing in the heap
 */
public class HeapArray extends StructuredValue implements StructuredArray {
	
	private Map<Integer, Slot> elements = new HashMap<Integer, Slot>();

	public HeapArray(Universe universe, Integer reference, ElementInfo elementInfo) {
		super(universe, new HeapObjectReference(reference), elementInfo);
	}

    @Override
    protected final int compareSignatureTo(StructuredValue value) {
        if (value instanceof HeapArray) {
            HeapArray array = (HeapArray) value;

            String name1 = getElementInfo().getClassInfo().getName();
            String name2 = array.getElementInfo().getClassInfo().getName();

            int typeDifference = name1.compareTo(name2);

            if (typeDifference == 0) return getLength().compareTo(array.getLength());

            return typeDifference;
        }

        return compareClasses(value);
    }

    @Override
    protected final int compareSlots(StructuredValue value) {
        //HeapArray array = (HeapArray) value;

        return Integer.valueOf(hashCode()).compareTo(value.hashCode());
    }
	
	@Override
	public HeapObjectReference getReference() {
		return (HeapObjectReference) super.getReference();
	}

	@Override
	public void setElement(Integer index, StructuredValue... values) {
		elements.put(index, new StructuredValueSlot(this, index, values));
	}
	
	@Override
	public void setElement(Integer index, PrimitiveValue... values) {
		elements.put(index, new PrimitiveValueSlot(this, index, values));
	}
	
	@Override
	public Slot getElement(Integer index) {
		return elements.get(index);
	}
	
	@Override
	public Map<Integer, Slot> getElements() {
		return elements;
	}
	
	@Override
	public Integer getLength() {
		return getElementInfo().arrayLength();
	}
	
	@Override
	public HeapArray cloneInto(Universe universe, Slot slot) {
		HeapArray clone = cloneInto(universe);
		
		clone.addSlot(slot);
		
		return clone;
	}
	
	@Override
	public HeapArray cloneInto(Universe universe) {
		boolean existed = universe.contains(getReference());
		
		HeapObjectReference reference = getReference();
		
		HeapArray clone = universe.getFactory().createArray(reference.getReference(), getElementInfo());
				
		if (!existed) {
			for (Integer index : elements.keySet()) {
				Slot slotClone = elements.get(index).cloneInto(universe, clone);
				
				clone.elements.put(index, slotClone);
			}
		}
		
		return clone;
	}

}
