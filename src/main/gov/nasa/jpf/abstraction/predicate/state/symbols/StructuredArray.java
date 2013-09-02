package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.Map;

public interface StructuredArray {
	public void setElement(Integer index, HeapValue... values);
	public void setElement(Integer index, PrimitiveValue... values);
	public Slot getElement(Integer index);
	public Map<Integer, Slot> getElements();
	public Integer getLength();
}
