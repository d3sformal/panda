package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.HashMap;
import java.util.Map;

public class HeapArray extends HeapValue implements StructuredArray {
	
	private Map<Integer, Slot> elements = new HashMap<Integer, Slot>();
	private Integer length;

	public HeapArray(Integer reference, Integer length) {
		super(new HeapObjectReference(reference));
		
		this.length = length;
	}
	
	@Override
	public void setElement(Integer index, HeapValue... values) {
		elements.put(index, new HeapValueSlot(this, index, values));
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
		return length;
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
		
		HeapObjectReference reference = (HeapObjectReference) getReference();
		
		HeapArray clone = universe.getFactory().createArray(reference.getReference(), getLength());
				
		if (!existed) {
			for (Integer index : elements.keySet()) {
				Slot slotClone = elements.get(index).cloneInto(universe, clone);
				
				clone.elements.put(index, slotClone);
			}
		}
		
		return clone;
	}

}
