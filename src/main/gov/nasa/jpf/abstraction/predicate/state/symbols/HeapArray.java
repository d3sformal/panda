package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.HashMap;
import java.util.Map;

public class HeapArray extends HeapValue {
	
	private Map<Integer, Slot> elements = new HashMap<Integer, Slot>();
	private Integer length;

	public HeapArray(Integer reference, Integer length) {
		super(reference);
		
		this.length = length;
	}
	
	public void setElement(Integer index, HeapValue... values) {
		elements.put(index, new HeapValueSlot(this, index, values));
	}
	
	public void setElement(Integer index, PrimitiveValue... values) {
		elements.put(index, new PrimitiveValueSlot(this, index));
	}
	
	public Slot getElement(Integer index) {
		return elements.get(index);
	}
	
	public Map<Integer, Slot> getElements() {
		return elements;
	}
	
	public Integer getLength() {
		return length;
	}

}
